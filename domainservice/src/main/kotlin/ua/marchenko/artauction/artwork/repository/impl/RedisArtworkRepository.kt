package ua.marchenko.artauction.artwork.repository.impl

import com.fasterxml.jackson.databind.ObjectMapper
import io.lettuce.core.RedisException
import java.net.SocketException
import java.time.Duration
import org.slf4j.LoggerFactory
import org.springframework.dao.QueryTimeoutException
import org.springframework.data.redis.RedisConnectionFailureException
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import reactor.util.retry.Retry
import ua.marchenko.artauction.artwork.model.MongoArtwork
import ua.marchenko.artauction.artwork.model.projection.ArtworkFull
import ua.marchenko.artauction.artwork.repository.ArtworkRepository
import ua.marchenko.core.artwork.enums.ArtworkStatus
import ua.marchenko.core.artwork.exception.ArtworkNotFoundException

@Repository
internal class RedisArtworkRepository(
    private val mongoArtworkRepository: MongoArtworkRepository,
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, ByteArray>,
    private val objectMapper: ObjectMapper,
) : ArtworkRepository by mongoArtworkRepository {

    override fun save(artwork: MongoArtwork): Mono<MongoArtwork> =
        mongoArtworkRepository.save(artwork)
            .doOnSuccess { saveToRedisWithRetries(it) }

    override fun findById(id: String): Mono<MongoArtwork> {
        return reactiveRedisTemplate.opsForValue().get(createGeneralKeyById(id))
            .handle { item, sink ->
                if (item.isNotEmpty()) {
                    runCatching { objectMapper.readValue(item, MongoArtwork::class.java) }
                        .onSuccess { artwork -> sink.next(artwork) }
                        .onFailure { error -> sink.error(error) }
                } else {
                    sink.error(ArtworkNotFoundException(artworkId = id))
                }
            }.switchIfEmpty {
                mongoArtworkRepository.findById(id)
                    .flatMap { saveArtworkToRedis(it).thenReturn(it) }
                    .switchIfEmpty {
                        saveEmptyArtworkToRedis(id)
                            .onErrorResume(::isErrorFromRedis) { ArtworkNotFoundException(artworkId = id).toMono() }
                            .then(ArtworkNotFoundException(artworkId = id).toMono())
                    }
            }.onErrorResume(::isErrorFromRedis) { mongoArtworkRepository.findById(id) }
    }

    override fun findFullById(id: String): Mono<ArtworkFull> {
        return reactiveRedisTemplate.opsForValue().get(createFullKeyById(id))
            .handle { item, sink ->
                if (item.isNotEmpty()) {
                    runCatching { objectMapper.readValue(item, ArtworkFull::class.java) }
                        .onSuccess { artwork -> sink.next(artwork) }
                        .onFailure { error -> sink.error(error) }
                } else {
                    sink.error(ArtworkNotFoundException(artworkId = id))
                }
            }.switchIfEmpty {
                mongoArtworkRepository.findFullById(id)
                    .flatMap { saveArtworkFullToRedis(it).thenReturn(it) }
                    .switchIfEmpty {
                        saveEmptyArtworkToRedis(id)
                            .onErrorResume(::isErrorFromRedis) { ArtworkNotFoundException(artworkId = id).toMono() }
                            .then(ArtworkNotFoundException(artworkId = id).toMono())
                    }
            }.onErrorResume(::isErrorFromRedis) { mongoArtworkRepository.findFullById(id) }
    }

    override fun updateById(id: String, artwork: MongoArtwork): Mono<MongoArtwork> =
        mongoArtworkRepository.updateById(id, artwork)
            .doOnSuccess { deleteKeysFromRedisWithRetries(createGeneralKeyById(id), createFullKeyById(id)) }

    override fun updateStatusByIdAndPreviousStatus(id: String, prevStatus: ArtworkStatus, newStatus: ArtworkStatus):
            Mono<MongoArtwork> =
        mongoArtworkRepository.updateStatusByIdAndPreviousStatus(id, prevStatus, newStatus)
            .doOnSuccess { deleteKeysFromRedisWithRetries(createGeneralKeyById(id), createFullKeyById(id)) }

    private fun saveToRedisWithRetries(artwork: MongoArtwork) {
        val id = requireNotNull(artwork.id) { "artwork id cannot be null" }.toHexString()
        callWithRetries(
            action = {
                reactiveRedisTemplate.delete(createFullKeyById(id))
                    .then(saveArtworkToRedis(artwork))
            },
            successLog = "Artwork with ID $id saved to Redis",
            errorLog = "Failed to save artwork with ID $id to Redis",
        )
    }

    private fun deleteKeysFromRedisWithRetries(vararg keys: String) {
        callWithRetries(
            action = { reactiveRedisTemplate.delete(*keys).thenReturn(Unit) },
            successLog = "Keys ${keys.joinToString()} removed from redis",
            errorLog = "Failed to remove keys ${keys.joinToString()} in Redis",
        )
    }

    private fun callWithRetries(
        action: () -> Mono<Unit>,
        successLog: String,
        errorLog: String,
        retry: Retry = retryOnRedisError(),
    ) {
        action()
            .retryWhen(retry)
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe(
                { log.info(successLog) },
                { log.error(errorLog, it) }
            )
    }

    private fun retryOnRedisError(maxAttempts: Long = 5, fixedDelay: Duration = Duration.ofSeconds(2)): Retry =
        Retry.fixedDelay(maxAttempts, fixedDelay)
            .filter { error -> isErrorFromRedis(error) }
            .doBeforeRetry { log.warn("Retrying due to error: ${it.failure().message}") }

    private fun saveEmptyArtworkToRedis(id: String): Mono<Unit> =
        saveToRedis(createGeneralKeyById(id), byteArrayOf())
            .then(saveToRedis(createFullKeyById(id), byteArrayOf()))

    private fun saveArtworkToRedis(artwork: MongoArtwork): Mono<Unit> {
        val id = requireNotNull(artwork.id) { "artwork id cannot be null" }.toHexString()
        return saveToRedis(createGeneralKeyById(id), objectMapper.writeValueAsBytes(artwork))
    }

    private fun saveArtworkFullToRedis(artwork: ArtworkFull): Mono<Unit> {
        val id = requireNotNull(artwork.id) { "artwork id cannot be null" }.toHexString()
        return saveToRedis(createFullKeyById(id), objectMapper.writeValueAsBytes(artwork))
    }

    private fun saveToRedis(key: String, value: ByteArray): Mono<Unit> =
        reactiveRedisTemplate.opsForValue().set(key, value, durationToLive)
            .doOnError { log.error("Error while saving to Redis: ${it.message}", it) }
            .thenReturn(Unit)

    private fun isErrorFromRedis(throwable: Throwable): Boolean =
        throwable::class in listOf(
            RedisConnectionFailureException::class,
            RedisException::class,
            SocketException::class,
            QueryTimeoutException::class,
        )

    companion object {
        private val durationToLive = Duration.ofMinutes(10)
        private const val KEY_PREFIX_GENERAL = "artwork"
        private const val KEY_PREFIX_FULL = "artwork-full"
        fun createGeneralKeyById(id: String): String = "$KEY_PREFIX_GENERAL:$id"
        fun createFullKeyById(id: String): String = "$KEY_PREFIX_FULL:$id"
        private val log = LoggerFactory.getLogger(RedisArtworkRepository::class.java)
    }
}
