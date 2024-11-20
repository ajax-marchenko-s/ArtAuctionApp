package ua.marchenko.artauction.artwork.repository

import com.fasterxml.jackson.databind.ObjectMapper
import java.time.Duration
import kotlin.test.Test
import kotlin.test.assertContentEquals
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.ReactiveRedisTemplate
import reactor.kotlin.test.test
import ua.marchenko.artauction.artwork.model.MongoArtwork
import ua.marchenko.artauction.artwork.model.projection.ArtworkFull
import ua.marchenko.artauction.artwork.random
import ua.marchenko.artauction.artwork.repository.impl.RedisArtworkRepository.Companion.createFullKeyById
import ua.marchenko.artauction.artwork.repository.impl.RedisArtworkRepository.Companion.createGeneralKeyById
import ua.marchenko.artauction.artwork.toFullArtwork
import ua.marchenko.artauction.common.AbstractBaseIntegrationTest
import ua.marchenko.artauction.user.model.MongoUser
import ua.marchenko.artauction.user.random
import ua.marchenko.artauction.user.repository.UserRepository
import ua.marchenko.core.artwork.enums.ArtworkStatus
import ua.marchenko.core.artwork.exception.ArtworkNotFoundException

class RedisArtworkRepositoryTest : AbstractBaseIntegrationTest {

    @Autowired
    private lateinit var redisArtworkRepository: ArtworkRepository

    @Autowired
    private lateinit var mongoArtworkRepository: ArtworkRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var reactiveRedisTemplate: ReactiveRedisTemplate<String, ByteArray>

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `should add artwork general to redis and remove empty bytearray from artwork full`() {
        // GIVEN
        val artwork = MongoArtwork.random()

        reactiveRedisTemplate.opsForValue().set(
            createGeneralKeyById(artwork.id!!.toHexString()),
            objectMapper.writeValueAsBytes(byteArrayOf()),
            timeToLive
        ).block()

        reactiveRedisTemplate.opsForValue().set(
            createFullKeyById(artwork.id!!.toHexString()),
            objectMapper.writeValueAsBytes(byteArrayOf()),
            timeToLive
        ).block()


        // WHEN
        val savedArtwork = redisArtworkRepository.save(artwork)

        // THEN
        savedArtwork.test()
            .expectNext(artwork)
            .verifyComplete()

        reactiveRedisTemplate.opsForValue()
            .get(createFullKeyById(artwork.id!!.toHexString())).test()
            .verifyComplete()

        reactiveRedisTemplate.opsForValue()
            .get(createGeneralKeyById(artwork.id!!.toHexString())).test()
            .assertNext { assertContentEquals(objectMapper.writeValueAsBytes(artwork), it) }
            .verifyComplete()
    }

    @Test
    fun `should find artwork in mongo and put to redis when redis doesnt have this artwork`() {
        // GIVEN
        val savedArtwork = mongoArtworkRepository.save(MongoArtwork.random(id = null)).block()!!

        // WHEN
        val result = redisArtworkRepository.findById(savedArtwork.id!!.toHexString())

        // THEN
        result.test()
            .expectNext(savedArtwork)
            .verifyComplete()

        val dataInRedis = reactiveRedisTemplate.opsForValue()
            .get(createGeneralKeyById(savedArtwork.id!!.toHexString()))

        dataInRedis.test()
            .assertNext { assertContentEquals(objectMapper.writeValueAsBytes(savedArtwork), it) }
            .verifyComplete()
    }

    @Test
    fun `should find artwork in redis when redis have this artwork`() {
        // GIVEN
        val savedArtwork = MongoArtwork.random()
        reactiveRedisTemplate.opsForValue().set(
            createGeneralKeyById(savedArtwork.id!!.toHexString()),
            objectMapper.writeValueAsBytes(savedArtwork),
            timeToLive
        ).block()

        // WHEN
        val result = redisArtworkRepository.findById(savedArtwork.id!!.toHexString())

        // THEN
        result.test()
            .expectNext(savedArtwork)
            .verifyComplete()
    }

    @Test
    fun `should set empty array in redis and throw exception when call findById on non-existing artwork first time`() {
        // GIVEN
        val id = ObjectId().toHexString() //random id

        // WHEN
        val result = redisArtworkRepository.findById(id)

        // THEN
        result.test()
            .verifyError(ArtworkNotFoundException::class.java)

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
        val savedArtist = userRepository.save(MongoUser.random(id = null)).block()
        val artwork = mongoArtworkRepository.save(
            MongoArtwork.random(id = null, artistId = savedArtist!!.id.toString())
        ).block()!!.toFullArtwork(savedArtist)

        // WHEN
        val result = redisArtworkRepository.findFullById(artwork.id.toString())

        // THEN
        result.test()
            .expectNext(artwork)
            .verifyComplete()

        val dataInRedis = reactiveRedisTemplate.opsForValue()
            .get(createFullKeyById(artwork.id!!.toHexString()))

        dataInRedis.test()
            .assertNext { assertContentEquals(objectMapper.writeValueAsBytes(artwork), it) }
            .verifyComplete()
    }

    @Test
    fun `should find full artwork in redis when redis have this artwork`() {
        // GIVEN
        val artwork = ArtworkFull.random()
        reactiveRedisTemplate.opsForValue().set(
            createFullKeyById(artwork.id!!.toHexString()),
            objectMapper.writeValueAsBytes(artwork),
            timeToLive
        ).block()

        // WHEN
        val result = redisArtworkRepository.findFullById(artwork.id!!.toHexString())

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
            Duration.ofMinutes(10)
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
        val id = ObjectId().toHexString()
        val artwork = MongoArtwork.random(id = id)
        val artworkFull = ArtworkFull.random(id = id)

        reactiveRedisTemplate.opsForValue().set(
            createGeneralKeyById(id),
            objectMapper.writeValueAsBytes(artwork),
            timeToLive
        ).block()

        reactiveRedisTemplate.opsForValue().set(
            createFullKeyById(id),
            objectMapper.writeValueAsBytes(artworkFull),
            timeToLive
        ).block()

        // WHEN
        redisArtworkRepository.updateById(artwork.id!!.toHexString(), MongoArtwork.random()).block()

        // THEN
        reactiveRedisTemplate.opsForValue()
            .get(createFullKeyById(artwork.id!!.toHexString())).test()
            .verifyComplete()

        reactiveRedisTemplate.opsForValue()
            .get(createGeneralKeyById(artwork.id!!.toHexString())).test()
            .verifyComplete()
    }

    @Test
    fun `should remove general and full key from redis when updating status of artwork`() {
        // GIVEN
        val id = ObjectId().toHexString()
        val artwork = MongoArtwork.random(id = id)
        val artworkFull = ArtworkFull.random(id = id)

        reactiveRedisTemplate.opsForValue().set(
            createGeneralKeyById(id),
            objectMapper.writeValueAsBytes(artwork),
            timeToLive
        ).block()

        reactiveRedisTemplate.opsForValue().set(
            createFullKeyById(id),
            objectMapper.writeValueAsBytes(artworkFull),
            timeToLive
        ).block()

        // WHEN
        redisArtworkRepository.updateStatusByIdAndPreviousStatus(
            artwork.id!!.toHexString(),
            ArtworkStatus.VIEW,
            ArtworkStatus.ON_AUCTION
        ).block()

        // THEN
        reactiveRedisTemplate.opsForValue()
            .get(createFullKeyById(artwork.id!!.toHexString())).test()
            .verifyComplete()

        reactiveRedisTemplate.opsForValue()
            .get(createGeneralKeyById(artwork.id!!.toHexString())).test()
            .verifyComplete()
    }

    companion object {
        private val timeToLive = Duration.ofMinutes(10)
    }
}
