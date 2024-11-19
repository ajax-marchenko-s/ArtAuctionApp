package ua.marchenko.artauction.artwork.repository.impl

import com.fasterxml.jackson.databind.ObjectMapper
import io.lettuce.core.RedisException
import java.net.SocketException
import java.time.Duration
import org.slf4j.LoggerFactory
import org.springframework.data.redis.RedisConnectionFailureException
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
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
            .flatMap { savedArtwork ->
                val id = requireNotNull(savedArtwork.id) { "artwork id cannot be null" }.toHexString()
                reactiveRedisTemplate.delete(createFullKeyById(id))
                    .then(saveArtworkToRedis(savedArtwork))
                    .thenReturn(savedArtwork)
                    .onErrorResume(::isErrorFromRedis) { savedArtwork.toMono() }
            }

    override fun findById(id: String): Mono<MongoArtwork> {
        return reactiveRedisTemplate.opsForValue()
            .get(createGeneralKeyById(id))
            .handle { item, sink ->
                if (item.isEmpty()) {
                    sink.error(ArtworkNotFoundException(artworkId = id))
                } else {
                    runCatching { objectMapper.readValue(item, MongoArtwork::class.java) }
                        .onSuccess { artwork -> sink.next(artwork) }
                        .onFailure { error -> sink.error(error) }
                }
            }.switchIfEmpty {
                mongoArtworkRepository.findById(id)
                    .flatMap { saveArtworkToRedis(it).thenReturn(it) }
                    .switchIfEmpty {
                        saveToRedis(createGeneralKeyById(id), byteArrayOf())
                            .then(saveToRedis(createFullKeyById(id), byteArrayOf()))
                            .onErrorResume(::isErrorFromRedis) { Mono.empty() }
                            .then(Mono.empty())
                    }
            }.onErrorResume(::isErrorFromRedis) { mongoArtworkRepository.findById(id) }
    }

    override fun findFullById(id: String): Mono<ArtworkFull> {
        return reactiveRedisTemplate.opsForValue()
            .get(createFullKeyById(id))
            .handle { item, sink ->
                if (item.isEmpty()) {
                    sink.error(ArtworkNotFoundException(artworkId = id))
                } else {
                    runCatching { objectMapper.readValue(item, ArtworkFull::class.java) }
                        .onSuccess { artwork -> sink.next(artwork) }
                        .onFailure { error -> sink.error(error) }
                }
            }.switchIfEmpty {
                mongoArtworkRepository.findFullById(id)
                    .flatMap { saveArtworkFullToRedis(it).thenReturn(it) }
                    .switchIfEmpty {
                        saveToRedis(createGeneralKeyById(id), byteArrayOf())
                            .then(saveToRedis(createFullKeyById(id), byteArrayOf()))
                            .onErrorResume(::isErrorFromRedis) { Mono.empty() }
                            .then(Mono.empty())

                    }
            }.onErrorResume(::isErrorFromRedis) { mongoArtworkRepository.findFullById(id) }
    }

    override fun updateById(id: String, artwork: MongoArtwork): Mono<MongoArtwork> {
        return reactiveRedisTemplate
            .delete(createGeneralKeyById(id), createFullKeyById(id))
            .then(mongoArtworkRepository.updateById(id, artwork))
            .onErrorResume(::isErrorFromRedis) { mongoArtworkRepository.updateById(id, artwork) }
    }

    override fun updateStatusByIdAndPreviousStatus(id: String, prevStatus: ArtworkStatus, newStatus: ArtworkStatus):
            Mono<MongoArtwork> {
        return reactiveRedisTemplate
            .delete(createGeneralKeyById(id), createFullKeyById(id))
            .then(mongoArtworkRepository.updateStatusByIdAndPreviousStatus(id, prevStatus, newStatus))
            .onErrorResume(::isErrorFromRedis) {
                mongoArtworkRepository.updateStatusByIdAndPreviousStatus(id, prevStatus, newStatus)
            }
    }

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
            .doOnError { error ->
                log.error("Error while saving to Redis: ${error.message}")
            }.thenReturn(Unit)

    private fun isErrorFromRedis(throwable: Throwable): Boolean {
        return listOf(
            RedisConnectionFailureException::class,
            RedisException::class,
            SocketException::class,
        ).any { it.isInstance(throwable) }
    }

    companion object {
        private val durationToLive = Duration.ofMinutes(10)
        private const val KEY_PREFIX_GENERAL = "artwork"
        private const val KEY_PREFIX_FULL = "artwork-full"
        fun createGeneralKeyById(id: String): String = "$KEY_PREFIX_GENERAL:$id"
        fun createFullKeyById(id: String): String = "$KEY_PREFIX_FULL:$id"
        private val log = LoggerFactory.getLogger(RedisArtworkRepository::class.java)
    }
}
