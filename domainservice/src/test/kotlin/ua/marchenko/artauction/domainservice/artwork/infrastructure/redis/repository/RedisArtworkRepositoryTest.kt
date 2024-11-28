package ua.marchenko.artauction.domainservice.artwork.infrastructure.redis.repository

import com.fasterxml.jackson.databind.ObjectMapper
import java.time.Duration
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.awaitility.Awaitility.await
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.ReactiveRedisTemplate
import reactor.kotlin.test.test
import ua.marchenko.artauction.domainservice.artwork.domain.random
import ua.marchenko.artauction.domainservice.artwork.domain.toFullArtwork
import ua.marchenko.artauction.domainservice.user.domain.User
import ua.marchenko.artauction.domainservice.user.infrastructure.mongo.repository.MongoUserRepository
import ua.marchenko.artauction.core.artwork.exception.ArtworkNotFoundException
import ua.marchenko.artauction.domainservice.artwork.application.port.output.ArtworkRepositoryOutputPort
import ua.marchenko.artauction.domainservice.artwork.domain.Artwork
import ua.marchenko.artauction.domainservice.artwork.domain.Artwork.ArtworkStatus
import ua.marchenko.artauction.domainservice.artwork.domain.projection.ArtworkFull
import ua.marchenko.artauction.domainservice.artwork.infrastructure.mongo.repository.MongoArtworkRepository
import ua.marchenko.artauction.domainservice.artwork.infrastructure.redis.RedisArtworkRepository.Companion.createFullKeyById
import ua.marchenko.artauction.domainservice.artwork.infrastructure.redis.RedisArtworkRepository.Companion.createGeneralKeyById
import ua.marchenko.artauction.domainservice.utils.AbstractBaseIntegrationTest
import ua.marchenko.artauction.domainservice.user.domain.random

class RedisArtworkRepositoryTest : AbstractBaseIntegrationTest {

    @Autowired
    private lateinit var redisArtworkRepository: ArtworkRepositoryOutputPort

    @Autowired
    private lateinit var mongoArtworkRepository: MongoArtworkRepository

    @Autowired
    private lateinit var userRepository: MongoUserRepository

    @Autowired
    private lateinit var reactiveRedisTemplate: ReactiveRedisTemplate<String, ByteArray>

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `should add artwork general to redis and remove empty bytearray from artwork full`() {
        // GIVEN
        val artwork = Artwork.random()

        reactiveRedisTemplate.opsForValue().set(
            createGeneralKeyById(artwork.id!!),
            byteArrayOf(),
            timeToLive
        ).block()

        reactiveRedisTemplate.opsForValue().set(
            createFullKeyById(artwork.id!!),
            byteArrayOf(),
            timeToLive
        ).block()

        // WHEN
        val savedArtwork = redisArtworkRepository.save(artwork).block()!!

        // THEN
        assertEquals(artwork.copy(id = savedArtwork.id), savedArtwork)
        await().atMost(timeToWait).untilAsserted {
            assertNull(
                reactiveRedisTemplate.opsForValue().get(createGeneralKeyById(artwork.id!!)).block(),
                "General key for artwork with ID ${artwork.id!!} should be deleted"
            )

            assertNull(
                reactiveRedisTemplate.opsForValue().get(createFullKeyById(artwork.id!!)).block(),
                "Full key for artwork with ID ${artwork.id!!} should be deleted"
            )
        }
    }

    @Test
    fun `should find artwork in mongo and put to redis when redis doesnt have this artwork`() {
        // GIVEN
        val savedArtwork = mongoArtworkRepository.save(Artwork.random(id = null)).block()!!

        // WHEN
        val result = redisArtworkRepository.findById(savedArtwork.id!!)

        // THEN
        result.test()
            .expectNext(savedArtwork)
            .verifyComplete()

        val dataInRedis = reactiveRedisTemplate.opsForValue()
            .get(createGeneralKeyById(savedArtwork.id!!))

        dataInRedis.test()
            .assertNext { assertContentEquals(objectMapper.writeValueAsBytes(savedArtwork), it) }
            .verifyComplete()
    }

    @Test
    fun `should find artwork in redis when redis have this artwork`() {
        // GIVEN
        val savedArtwork = Artwork.random()
        reactiveRedisTemplate.opsForValue().set(
            createGeneralKeyById(savedArtwork.id!!),
            objectMapper.writeValueAsBytes(savedArtwork),
            timeToLive
        ).block()

        // WHEN
        val result = redisArtworkRepository.findById(savedArtwork.id!!)

        // THEN
        result.test()
            .expectNext(savedArtwork)
            .verifyComplete()
    }

    @Test
    fun `should set empty array in redis and return empty when call findById on non-existing artwork first time`() {
        // GIVEN
        val id = ObjectId().toHexString() //random id

        // WHEN
        val result = redisArtworkRepository.findById(id)

        // THEN
        result.test()
            .verifyComplete()

        val dataInRedis = reactiveRedisTemplate.opsForValue()
            .get(createGeneralKeyById(id))

        dataInRedis.test()
            .assertNext { assertContentEquals(byteArrayOf(), it) }
            .verifyComplete()
    }

    @Test
    fun `should throw ArtworkNotFound when call findById on non-existing artwork and empty array is set in redis`() {
        // GIVEN
        val id = ObjectId().toHexString() //random id
        reactiveRedisTemplate.opsForValue().set(
            createGeneralKeyById(id),
            byteArrayOf(),
            timeToLive
        ).block()

        // WHEN
        val result = redisArtworkRepository.findById(id)

        // THEN
        result.test()
            .verifyError(ArtworkNotFoundException::class.java)
    }

    @Test
    fun `should find full artwork in mongo and put to redis when redis doesnt have this artwork`() {
        // GIVEN
        val savedArtist = userRepository.save(User.random(id = null)).block()
        val artwork = mongoArtworkRepository.save(
            Artwork.random(id = null, artistId = savedArtist!!.id.toString())
        ).block()!!.toFullArtwork(savedArtist)

        // WHEN
        val result = redisArtworkRepository.findFullById(artwork.id)

        // THEN
        result.test()
            .expectNext(artwork)
            .verifyComplete()

        val dataInRedis = reactiveRedisTemplate.opsForValue()
            .get(createFullKeyById(artwork.id))

        dataInRedis.test()
            .assertNext { assertContentEquals(objectMapper.writeValueAsBytes(artwork), it) }
            .verifyComplete()
    }

    @Test
    fun `should find full artwork in redis when redis have this artwork`() {
        // GIVEN
        val artwork = ArtworkFull.random()
        reactiveRedisTemplate.opsForValue().set(
            createFullKeyById(artwork.id),
            objectMapper.writeValueAsBytes(artwork),
            timeToLive
        ).block()

        // WHEN
        val result = redisArtworkRepository.findFullById(artwork.id)

        // THEN
        result.test()
            .expectNext(artwork)
            .verifyComplete()
    }

    @Test
    fun `should throw ArtworkNotFound when call findFullById and empty array for key is set in redis`() {
        // GIVEN
        val id = ObjectId().toHexString() //random id
        reactiveRedisTemplate.opsForValue().set(
            createFullKeyById(id),
            byteArrayOf(),
            timeToLive
        ).block()

        // WHEN
        val result = redisArtworkRepository.findFullById(id)

        // THEN
        result.test()
            .verifyError(ArtworkNotFoundException::class.java)
    }

    @Test
    fun `should remove general and full key from redis when updating artwork`() {
        // GIVEN
        val savedArtwork = mongoArtworkRepository.save(Artwork.random(id = null)).block()!!

        reactiveRedisTemplate.opsForValue().set(
            createGeneralKeyById(savedArtwork.id!!),
            objectMapper.writeValueAsBytes(savedArtwork),
            timeToLive
        ).block()

        reactiveRedisTemplate.opsForValue().set(
            createFullKeyById(savedArtwork.id!!),
            objectMapper.writeValueAsBytes(savedArtwork),
            timeToLive
        ).block()

        // WHEN
        redisArtworkRepository.updateById(savedArtwork.id!!, Artwork.random()).block()

        // THEN
        await().atMost(timeToWait).untilAsserted {
            assertNull(
                reactiveRedisTemplate.opsForValue().get(createGeneralKeyById(savedArtwork.id!!)).block(),
                "General key for artwork with ID ${savedArtwork.id} should be deleted"
            )
            assertNull(
                reactiveRedisTemplate.opsForValue().get(createFullKeyById(savedArtwork.id!!)).block(),
                "Full key for artwork with ID ${savedArtwork.id} should be deleted"
            )
        }
    }

    @Test
    fun `should remove general and full key from redis when updating status of artwork`() {
        // GIVEN
        val savedArtwork =
            mongoArtworkRepository.save(Artwork.random(id = null, status = ArtworkStatus.VIEW)).block()!!

        reactiveRedisTemplate.opsForValue().set(
            createGeneralKeyById(savedArtwork.id!!),
            objectMapper.writeValueAsBytes(savedArtwork),
            timeToLive
        ).block()

        reactiveRedisTemplate.opsForValue().set(
            createFullKeyById(savedArtwork.id!!),
            objectMapper.writeValueAsBytes(savedArtwork),
            timeToLive
        ).block()

        // WHEN
        redisArtworkRepository.updateStatusByIdAndPreviousStatus(
            savedArtwork.id!!,
            ArtworkStatus.VIEW,
            ArtworkStatus.ON_AUCTION
        ).block()

        // THEN
        await().atMost(timeToWait).untilAsserted {
            assertNull(
                reactiveRedisTemplate.opsForValue().get(createGeneralKeyById(savedArtwork.id!!)).block(),
                "General key for artwork with ID ${savedArtwork.id} should be deleted"
            )
            assertNull(
                reactiveRedisTemplate.opsForValue().get(createFullKeyById(savedArtwork.id!!)).block(),
                "Full key for artwork with ID ${savedArtwork.id} should be deleted"
            )
        }
    }

    companion object {
        private val timeToLive = Duration.ofMinutes(10)
        private val timeToWait = Duration.ofSeconds(5)
    }
}
