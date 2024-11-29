package ua.marchenko.artauction.domainservice.artwork.infrastructure.redis

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
import reactor.util.retry.Retry
import ua.marchenko.artauction.domainservice.artwork.domain.Artwork.ArtworkStatus
import ua.marchenko.artauction.core.artwork.exception.ArtworkNotFoundException
import ua.marchenko.artauction.domainservice.artwork.application.port.output.ArtworkRepositoryOutputPort
import ua.marchenko.artauction.domainservice.artwork.domain.Artwork
import ua.marchenko.artauction.domainservice.artwork.domain.projection.ArtworkFull

@Suppress("TooManyFunctions")
@Repository
class RedisArtworkRepository(
    private val mongoArtworkRepository: ArtworkRepositoryOutputPort,
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, ByteArray>,
    private val objectMapper: ObjectMapper,
) : ArtworkRepositoryOutputPort by mongoArtworkRepository {

    override fun save(artwork: Artwork): Mono<Artwork> =
        mongoArtworkRepository.save(artwork)
            .doOnSuccess {
                deleteKeysFromRedisWithRetries(
                    createGeneralKeyById(requireNotNull(it.id)),
                    createFullKeyById(requireNotNull(it.id)),
                )
            }

    override fun findById(id: String): Mono<Artwork> {
        return reactiveRedisTemplate.opsForValue().get(createGeneralKeyById(id))
            .handle { item, sink ->
                if (item.isNotEmpty()) {
                    runCatching { objectMapper.readValue(item, Artwork::class.java) }
                        .onSuccess { artwork -> sink.next(artwork) }
                        .onFailure { error ->
                            sink.error(error)
                            deleteKeysFromRedisWithRetries(createGeneralKeyById(id))
                        }
                } else {
                    sink.error(ArtworkNotFoundException(artworkId = id))
                }
            }.switchIfEmpty {
                findByIdInMongoAndSaveToRedis(
                    id = id,
                    findByIdInMongo = mongoArtworkRepository::findById,
                    saveToRedis = ::saveArtworkToRedis,
                    saveEmptyToRedis = ::saveEmptyArtworkToRedis,
                )
            }.onErrorResume(::isErrorFromRedis) { mongoArtworkRepository.findById(id) }
    }

    override fun findFullById(id: String): Mono<ArtworkFull> {
        return reactiveRedisTemplate.opsForValue().get(createFullKeyById(id))
            .handle { item, sink ->
                if (item.isNotEmpty()) {
                    runCatching { objectMapper.readValue(item, ArtworkFull::class.java) }
                        .onSuccess { artwork -> sink.next(artwork) }
                        .onFailure { error ->
                            sink.error(error)
                            deleteKeysFromRedisWithRetries(createFullKeyById(id))
                        }
                } else {
                    sink.error(ArtworkNotFoundException(artworkId = id))
                }
            }.switchIfEmpty {
                findByIdInMongoAndSaveToRedis(
                    id = id,
                    findByIdInMongo = mongoArtworkRepository::findFullById,
                    saveToRedis = ::saveArtworkFullToRedis,
                    saveEmptyToRedis = ::saveEmptyArtworkToRedis,
                )
            }.onErrorResume(::isErrorFromRedis) { mongoArtworkRepository.findFullById(id) }
    }

    override fun updateById(id: String, artwork: Artwork): Mono<Artwork> =
        mongoArtworkRepository.updateById(id, artwork)
            .doOnSuccess { deleteKeysFromRedisWithRetries(createGeneralKeyById(id), createFullKeyById(id)) }

    override fun updateStatusByIdAndPreviousStatus(id: String, prevStatus: ArtworkStatus, newStatus: ArtworkStatus):
            Mono<Artwork> =
        mongoArtworkRepository.updateStatusByIdAndPreviousStatus(id, prevStatus, newStatus)
            .doOnSuccess { deleteKeysFromRedisWithRetries(createGeneralKeyById(id), createFullKeyById(id)) }

    private fun <T : Any> findByIdInMongoAndSaveToRedis(
        id: String,
        findByIdInMongo: (String) -> Mono<T>,
        saveToRedis: (T) -> Mono<Unit>,
        saveEmptyToRedis: (String) -> Mono<Unit>
    ): Mono<T> {
        return findByIdInMongo(id)
            .flatMap { saveToRedis(it).thenReturn(it) }
            .switchIfEmpty(
                saveEmptyToRedis(id)
                    .onErrorResume(::isErrorFromRedis) { Mono.empty() }
                    .then(Mono.empty())
            )
    }

    private fun deleteKeysFromRedisWithRetries(vararg keys: String) {
        callWithRetries(
            action = { reactiveRedisTemplate.unlink(*keys).thenReturn(Unit) },
            successLog = { "Keys ${keys.joinToString()} removed from redis" },
            errorLog = { "Failed to remove keys ${keys.joinToString()} in Redis" },
        )
    }

    private fun callWithRetries(
        action: () -> Mono<Unit>,
        successLog: () -> String,
        errorLog: () -> String,
        retry: Retry = retryOnRedisError(),
    ) {
        action()
            .retryWhen(retry)
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe(
                { log.info(successLog()) },
                { log.error(errorLog(), it) }
            )
    }

    private fun retryOnRedisError(maxAttempts: Long = 5, minBackoff: Duration = minBackoffDuration): Retry =
        Retry.backoff(maxAttempts, minBackoff)
            .filter { error -> isErrorFromRedis(error) }
            .doBeforeRetry { log.warn("Retrying due to error: {}", it) }

    private fun saveEmptyArtworkToRedis(id: String): Mono<Unit> =
        saveToRedis(createGeneralKeyById(id), byteArrayOf())
            .then(saveToRedis(createFullKeyById(id), byteArrayOf()))

    private fun saveArtworkToRedis(artwork: Artwork): Mono<Unit> {
        val id = requireNotNull(artwork.id) { "artwork id cannot be null" }
        return saveToRedis(createGeneralKeyById(id), objectMapper.writeValueAsBytes(artwork))
    }

    private fun saveArtworkFullToRedis(artwork: ArtworkFull): Mono<Unit> {
        val id = requireNotNull(artwork.id) { "artwork id cannot be null" }
        return saveToRedis(createFullKeyById(id), objectMapper.writeValueAsBytes(artwork))
    }

    private fun saveToRedis(key: String, value: ByteArray): Mono<Unit> =
        reactiveRedisTemplate.opsForValue().set(key, value, durationToLive)
            .doOnError { log.error("Error while saving to Redis: {}", it.message, it) }
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
        private val minBackoffDuration = Duration.ofMillis(200)
        private const val KEY_PREFIX_GENERAL = "artwork"
        private const val KEY_PREFIX_FULL = "artwork-full"
        fun createGeneralKeyById(id: String): String = "$KEY_PREFIX_GENERAL:$id"
        fun createFullKeyById(id: String): String = "$KEY_PREFIX_FULL:$id"
        private val log = LoggerFactory.getLogger(RedisArtworkRepository::class.java)
    }
}
