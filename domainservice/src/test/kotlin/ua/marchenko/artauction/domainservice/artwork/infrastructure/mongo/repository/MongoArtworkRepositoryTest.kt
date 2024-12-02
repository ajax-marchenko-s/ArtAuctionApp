package ua.marchenko.artauction.domainservice.artwork.infrastructure.mongo.repository

import ua.marchenko.artauction.domainservice.artwork.domain.random
import ua.marchenko.artauction.domainservice.artwork.domain.toFullArtwork
import ua.marchenko.artauction.domainservice.artwork.domain.Artwork
import ua.marchenko.artauction.domainservice.artwork.domain.Artwork.ArtworkStatus
import kotlin.test.Test
import kotlin.test.assertTrue
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.springframework.beans.factory.annotation.Autowired
import reactor.kotlin.test.test
import ua.marchenko.artauction.domainservice.artwork.domain.CreateArtwork
import ua.marchenko.artauction.domainservice.artwork.getRandomString
import ua.marchenko.artauction.domainservice.user.domain.CreateUser
import ua.marchenko.artauction.domainservice.utils.AbstractBaseIntegrationTest
import ua.marchenko.artauction.domainservice.user.domain.random
import ua.marchenko.artauction.domainservice.user.infrastructure.mongo.repository.MongoUserRepository

class MongoArtworkRepositoryTest : AbstractBaseIntegrationTest {

    @Autowired
    private lateinit var artworkRepository: MongoArtworkRepository

    @Autowired
    private lateinit var userRepository: MongoUserRepository

    @Test
    fun `should save artwork`() {
        // GIVEN
        val createArtwork = CreateArtwork.random()
        val expectedArtwork = Artwork(
            id = EMPTY_STRING,
            title = createArtwork.title,
            description = createArtwork.description,
            style = createArtwork.style,
            width = createArtwork.width,
            height = createArtwork.height,
            status = createArtwork.status,
            artistId = createArtwork.artistId,
        )

        // WHEN
        val savedArtwork = artworkRepository.save(createArtwork)

        // THEN
        savedArtwork.test()
            .assertNext { artworkFromMono ->
                assertEquals(
                    expectedArtwork.copy(id = artworkFromMono.id),
                    artworkFromMono
                )
            }
            .verifyComplete()
    }

    @Test
    fun `should find artwork by id when artwork with this id exists`() {
        // GIVEN
        val savedArtwork = artworkRepository.save(CreateArtwork.random()).block()

        // WHEN
        val result = artworkRepository.findById(savedArtwork!!.id)

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
        val savedArtist = userRepository.save(CreateUser.random()).block()
        val artwork = artworkRepository.save(
            CreateArtwork.random(artistId = savedArtist!!.id)
        ).block()!!.toFullArtwork(savedArtist)

        // WHEN
        val result = artworkRepository.findFullById(artwork.id)

        // THEN
        result.test()
            .expectNext(artwork)
            .verifyComplete()
    }

    @Test
    fun `should return empty when artist of artwork doesnt exist in db`() {
        // GIVEN
        val savedArtwork = artworkRepository.save(CreateArtwork.random()).block()

        // WHEN
        val result = artworkRepository.findFullById(savedArtwork!!.id)

        // THEN
        result.test()
            .verifyComplete()
    }

    @Test
    fun `should return all full artwork when they are exists`() {
        // GIVEN
        val savedArtist = userRepository.save(CreateUser.random()).block()
        val artworks = List(2) {
            artworkRepository.save(CreateArtwork.random(artistId = savedArtist!!.id)).block()!!
                .toFullArtwork(savedArtist)
        }

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
        val artworks = List(2) { artworkRepository.save(CreateArtwork.random()).block() }

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
        val savedArtwork = artworkRepository.save(CreateArtwork.random()).block()

        // WHEN
        val result = artworkRepository.existsById(savedArtwork!!.id)

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
            CreateArtwork.random()
        ).block()

        // WHEN
        val result = artworkRepository.updateStatusByIdAndPreviousStatus(
            savedArtwork!!.id,
            savedArtwork.status,
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
            CreateArtwork.random(status = ArtworkStatus.ON_AUCTION)
        ).block()

        // WHEN
        val result = artworkRepository.updateStatusByIdAndPreviousStatus(
            artwork!!.id,
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
            CreateArtwork.random()
        ).block()
        val updatedArtwork = Artwork.random()
        val nonUpdatableFields = listOf(
            Artwork::id.name,
            Artwork::status.name,
            Artwork::artistId.name
        )

        // WHEN
        val result = artworkRepository.updateById(savedArtwork!!.id, updatedArtwork, nonUpdatableFields)

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
        val result = artworkRepository.updateById(getRandomString(), Artwork.random(), emptyList())

        // THEN
        result.test()
            .verifyComplete()
    }

    companion object {
        private const val EMPTY_STRING = ""
    }
}
