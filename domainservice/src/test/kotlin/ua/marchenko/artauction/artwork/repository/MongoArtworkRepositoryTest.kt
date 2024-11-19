package ua.marchenko.artauction.artwork.repository

import ua.marchenko.artauction.artwork.random
import ua.marchenko.artauction.artwork.toFullArtwork
import ua.marchenko.artauction.getRandomString
import kotlin.test.Test
import kotlin.test.assertTrue
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import reactor.kotlin.test.test
import ua.marchenko.artauction.artwork.model.MongoArtwork
import ua.marchenko.artauction.common.AbstractBaseIntegrationTest
import ua.marchenko.artauction.user.model.MongoUser
import ua.marchenko.artauction.user.repository.UserRepository
import ua.marchenko.core.artwork.enums.ArtworkStatus
import ua.marchenko.artauction.user.random

class MongoArtworkRepositoryTest : AbstractBaseIntegrationTest {

    @Autowired
    @Qualifier("mongoArtworkRepository")
    private lateinit var artworkRepository: ArtworkRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `should save artwork`() {
        // GIVEN
        val artwork = MongoArtwork.random(id = null)

        // WHEN
        val savedArtwork = artworkRepository.save(artwork)

        // THEN
        savedArtwork.test()
            .assertNext { artworkFromMono -> assertEquals(artwork.copy(id = artworkFromMono.id), artworkFromMono) }
            .verifyComplete()
    }

    @Test
    fun `should find artwork by id when artwork with this id exists`() {
        // GIVEN
        val savedArtwork = artworkRepository.save(MongoArtwork.random(id = null)).block()

        // WHEN
        val result = artworkRepository.findById(savedArtwork!!.id.toString())

        // THEN
        result.test()
            .expectNext(savedArtwork)
            .verifyComplete()
    }

    @Test
    fun `should return empty when there is no artwork with this id`() {
        // WHEN
        val result = artworkRepository.findById(ObjectId().toString()) //random id

        // THEN
        result.test()
            .verifyComplete()
    }

    @Test
    fun `should return artwork with artist when artwork with this id exists`() {
        // GIVEN
        val savedArtist = userRepository.save(MongoUser.random(id = null)).block()
        val artwork = artworkRepository.save(
            MongoArtwork.random(id = null, artistId = savedArtist!!.id.toString())
        ).block()!!.toFullArtwork(savedArtist)

        // WHEN
        val result = artworkRepository.findFullById(artwork.id.toString())

        // THEN
        result.test()
            .expectNext(artwork)
            .verifyComplete()
    }

    @Test
    fun `should return empty when artist of artwork doesnt exist in db`() {
        // GIVEN
        val savedArtwork = artworkRepository.save(MongoArtwork.random(id = null)).block()

        // WHEN
        val result = artworkRepository.findFullById(savedArtwork!!.id.toString())

        // THEN
        result.test()
            .verifyComplete()
    }

    @Test
    fun `should return all full artwork when they are exists`() {
        // GIVEN
        val savedArtist = userRepository.save(MongoUser.random(id = null)).block()
        val artworks = listOf(
            artworkRepository.save(MongoArtwork.random(artistId = savedArtist!!.id.toString())).block()!!
                .toFullArtwork(savedArtist),
            artworkRepository.save(MongoArtwork.random(artistId = savedArtist.id.toString())).block()!!
                .toFullArtwork(savedArtist),
        )

        // WHEN
        val result = artworkRepository.findFullAll(page = 0, limit = 100).collectList()

        // THEN
        result.test()
            .assertNext { artworkList ->
                assertTrue(
                    artworkList.containsAll(artworks),
                    "Expected found artworks should contain $artworks"
                )
            }
            .verifyComplete()
    }

    @Test
    fun `should return all artworks when they are exists`() {
        // GIVEN
        val artworks = listOf(
            artworkRepository.save(MongoArtwork.random(id = null)).block(),
            artworkRepository.save(MongoArtwork.random(id = null)).block()
        )

        // WHEN
        val result = artworkRepository.findAll(page = 0, limit = 100).collectList()

        // THEN
        result.test()
            .assertNext { foundArtworks ->
                assertTrue(
                    foundArtworks.containsAll(artworks),
                    "Expected found artworks should contain $artworks"
                )
            }
            .verifyComplete()
    }

    @Test
    fun `should return true when artwork with this id exists`() {
        // GIVEN
        val savedArtwork = artworkRepository.save(MongoArtwork.random(id = null)).block()

        // WHEN
        val result = artworkRepository.existsById(savedArtwork!!.id.toString())

        // THEN
        result.test()
            .assertNext { existsRes -> assertTrue(existsRes, "Artwork with given id must exist") }
            .verifyComplete()
    }

    @Test
    fun `should return false when there is no artwork with this id`() {
        // WHEN
        val result = artworkRepository.existsById(ObjectId().toString())

        // THEN
        result.test()
            .assertNext { existsRes -> assertFalse(existsRes, "Artwork with given id must not exist") }
            .verifyComplete()
    }

    @Test
    fun `should set new status to artwork if artwork with previous status and id exist`() {
        // GIVEN
        val savedArtwork = artworkRepository.save(
            MongoArtwork.random(id = null, status = ArtworkStatus.VIEW)
        ).block()

        // WHEN
        val result = artworkRepository.updateStatusByIdAndPreviousStatus(
            savedArtwork!!.id!!.toHexString(),
            savedArtwork.status!!,
            ArtworkStatus.ON_AUCTION
        )

        // THEN
        result.test()
            .expectNext(savedArtwork.copy(status = ArtworkStatus.ON_AUCTION))
            .verifyComplete()
    }

    @Test
    fun `should return empty if artwork with previous status and id doesnt exist`() {
        // GIVEN
        val artwork = artworkRepository.save(
            MongoArtwork.random(id = null, status = ArtworkStatus.ON_AUCTION)
        ).block()

        // WHEN
        val result = artworkRepository.updateStatusByIdAndPreviousStatus(
            artwork!!.id!!.toHexString()!!,
            ArtworkStatus.VIEW,
            ArtworkStatus.ON_AUCTION
        )

        // THEN
        result.test()
            .verifyComplete()
    }

    @Test
    fun `should update artwork if artwork with id exist`() {
        // GIVEN
        val savedArtwork = artworkRepository.save(
            MongoArtwork.random(id = null, status = ArtworkStatus.ON_AUCTION)
        ).block()
        val updatedArtwork = MongoArtwork.random()

        // WHEN
        val result = artworkRepository.updateById(
            savedArtwork!!.id!!.toHexString(),
            updatedArtwork
        )

        // THEN
        result.test()
            .assertNext { artwork ->
                assertEquals(
                    updatedArtwork.copy(
                        id = savedArtwork.id,
                        status = savedArtwork.status,
                        artistId = savedArtwork.artistId
                    ), artwork
                )
            }
            .verifyComplete()
    }

    @Test
    fun `should return empty if artwork to update id doesnt exist`() {
        // WHEN
        val result = artworkRepository.updateById(getRandomString(), MongoArtwork.random())

        // THEN
        result.test()
            .verifyComplete()
    }
}
