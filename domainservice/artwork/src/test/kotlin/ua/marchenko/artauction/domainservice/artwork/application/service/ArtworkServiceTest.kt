package ua.marchenko.artauction.domainservice.artwork.application.service

import kotlin.test.Test
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verifyOrder
import org.junit.jupiter.api.Assertions.assertFalse
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertTrue
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test
import ua.marchenko.artauction.core.artwork.exception.ArtworkNotFoundException
import ua.marchenko.artauction.domainservice.artwork.application.mapper.toDomain
import ua.marchenko.artauction.domainservice.artwork.application.port.output.ArtworkRepositoryOutputPort
import ua.marchenko.artauction.domainservice.artwork.domain.Artwork
import ua.marchenko.artauction.domainservice.artwork.domain.Artwork.ArtworkStatus
import ua.marchenko.artauction.domainservice.artwork.domain.CreateArtwork
import ua.marchenko.artauction.domainservice.artwork.domain.projection.ArtworkFull
import ua.marchenko.artauction.domainservice.artwork.domain.random
import ua.marchenko.artauction.domainservice.user.application.service.UserService

class ArtworkServiceTest {

    @MockK
    private lateinit var mockArtworkRepository: ArtworkRepositoryOutputPort

    @MockK
    private lateinit var mockUserService: UserService

    @InjectMockKs
    private lateinit var artworkService: ArtworkService

    @Test
    fun `should return a list of artworks when artworks are present`() {
        // GIVEN
        val artworks = listOf(Artwork.random())
        every { mockArtworkRepository.findAll() } returns artworks.toFlux()

        // WHEN
        val result = artworkService.getAll()

        // THEN
        result.test()
            .expectNext(artworks[0])
            .verifyComplete()
    }

    @Test
    fun `should return an empty list of artworks when there are no artworks`() {
        // GIVEN
        every { mockArtworkRepository.findAll() } returns Flux.empty()

        // WHEN
        val result = artworkService.getAll()

        // THEN
        result.test()
            .verifyComplete()
    }

    @Test
    fun `should return artwork by id when artwork with this id exists`() {
        // GIVEN
        val id = ObjectId().toHexString()
        val artwork = Artwork.random(id = id)

        every { mockArtworkRepository.findById(id) } returns artwork.toMono()

        // WHEN
        val result = artworkService.getById(id)

        // THEN
        result.test()
            .expectNext(artwork)
            .verifyComplete()
    }

    @Test
    fun `should throw ArtworkNotFoundException when there is no artwork with this id`() {
        // GIVEN
        every { mockArtworkRepository.findById(any()) } returns Mono.empty()

        // WHEN
        val result = artworkService.getById(ObjectId().toHexString())

        // THEN
        result.test()
            .verifyError(ArtworkNotFoundException::class.java)
    }

    @Test
    fun `should return full artwork by id when artwork with this id exists`() {
        // GIVEN
        val id = ObjectId().toHexString()
        val artwork = ArtworkFull.random(id = id)

        every { mockArtworkRepository.findFullById(id) } returns artwork.toMono()

        // WHEN
        val result = artworkService.getFullById(id)

        // THEN
        result.test()
            .expectNext(artwork)
            .verifyComplete()
    }

    @Test
    fun `should throw ArtworkNotFoundException when there is no full artwork with this id`() {
        // GIVEN
        every { mockArtworkRepository.findFullById(any()) } returns Mono.empty()

        // WHEN
        val result = artworkService.getFullById(ObjectId().toHexString())

        // THEN
        result.test()
            .verifyError(ArtworkNotFoundException::class.java)
    }

    @Test
    fun `should return a list of full artworks when artworks are present`() {
        // GIVEN
        val artworks = listOf(ArtworkFull.random())
        every { mockArtworkRepository.findFullAll() } returns artworks.toFlux()

        // WHEN
        val result = artworkService.getFullAll()

        // THEN
        result.test()
            .expectNext(artworks[0])
            .verifyComplete()
    }

    @Test
    fun `should set status and call userService before calling repository method`() {
        // GIVEN
        val artistId = ObjectId().toHexString()
        val artworkToSave = CreateArtwork.random(artistId = artistId)
        val expectedArtwork = artworkToSave.toDomain(status = ArtworkStatus.VIEW)

        every { mockUserService.existById(artistId) } returns true.toMono()
        every { mockArtworkRepository.save(expectedArtwork) } returns expectedArtwork.toMono()

        // WHEN
        val result = artworkService.save(artworkToSave)

        // THEN
        result.test()
            .expectNext(expectedArtwork)
            .verifyComplete()
        verifyOrder {
            mockUserService.existById(artistId)
            mockArtworkRepository.save(expectedArtwork)
        }
    }

    @Test
    fun `should return false when there is no artwork with given id`() {
        // GIVEN
        every { mockArtworkRepository.existsById(any()) } returns false.toMono()

        // WHEN
        val result = artworkService.existsById(ObjectId().toHexString())

        // THEN
        result.test()
            .assertNext { assertFalse(it, "ExistsById should return false") }
            .verifyComplete()
    }

    @Test
    fun `should return true when artwork with this id exists`() {
        // GIVEN
        val id = ObjectId().toHexString()
        every { mockArtworkRepository.existsById(id) } returns true.toMono()

        // WHEN
        val result = artworkService.existsById(id)

        // THEN
        result.test()
            .assertNext { assertTrue(it, "ExistsById should return false") }
            .verifyComplete()
    }
}
