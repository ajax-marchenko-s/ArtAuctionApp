package ua.marchenko.artauction.artwork.repository

import artwork.random
import artwork.toFullArtwork
import kotlin.test.Test
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import reactor.kotlin.test.test
import ua.marchenko.artauction.artwork.model.MongoArtwork
import ua.marchenko.artauction.common.AbstractBaseIntegrationTest
import ua.marchenko.artauction.user.model.MongoUser
import ua.marchenko.artauction.user.repository.UserRepository
import user.random

class ArtworkRepositoryTest : AbstractBaseIntegrationTest {

    @Autowired
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
        val result = artworkRepository.findById(savedArtwork?.id.toString())

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
            MongoArtwork.random(id = null, artistId = savedArtist?.id.toString())
        ).block()?.toFullArtwork(savedArtist)

        // WHEN
        val result = artworkRepository.findFullById(artwork?.id.toString())

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
        val result = artworkRepository.findFullById(savedArtwork?.id.toString())

        // THEN
        result.test()
            .verifyComplete()
    }

    @Test
    fun `should return all full artwork when they are exists`() {
        // GIVEN
        val savedArtist = userRepository.save(MongoUser.random(id = null)).block()
        val artworks = listOf(
            artworkRepository.save(MongoArtwork.random(artistId = savedArtist?.id.toString())).block()
                ?.toFullArtwork(savedArtist),
            artworkRepository.save(MongoArtwork.random(artistId = savedArtist?.id.toString())).block()
                ?.toFullArtwork(savedArtist),
        )

        // WHEN
        val result = artworkRepository.findFullAll().collectList()

        // THEN
        result.test()
            .expectNextMatches { it.containsAll(artworks) }
            .`as`(
                "Artwork with title ${artworks[0]?.title} and ${artworks[1]?.title}," +
                        " artist ${savedArtist?.name} must be found"
            )
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
        val result = artworkRepository.findAll().collectList()

        // THEN
        result.test()
            .expectNextMatches { it.containsAll(artworks) }
            .`as`("Artwork with id ${artworks[0]?.id} and ${artworks[1]?.id} must be found")
            .verifyComplete()
    }

    @Test
    fun `should return true when artwork with this id exists`() {
        // GIVEN
        val savedArtwork = artworkRepository.save(MongoArtwork.random(id = null)).block()

        // WHEN
        val result = artworkRepository.existsById(savedArtwork?.id.toString())

        // THEN
        result.test()
            .expectNext(true)
            .`as`("Artwork with given id must exist")
            .verifyComplete()
    }

    @Test
    fun `should return false when there is no artwork with this id`() {
        // WHEN
        val result = artworkRepository.existsById(ObjectId().toString())

        // THEN
        result.test()
            .expectNext(false)
            .`as`("Artwork with given id must not exist")
            .verifyComplete()
    }
}
