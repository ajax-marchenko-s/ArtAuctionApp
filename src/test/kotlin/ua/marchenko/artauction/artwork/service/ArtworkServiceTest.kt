package ua.marchenko.artauction.artwork.service

import artwork.random
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import ua.marchenko.artauction.artwork.enums.ArtworkStatus
import ua.marchenko.artauction.artwork.exception.ArtworkNotFoundException
import ua.marchenko.artauction.artwork.model.MongoArtwork
import ua.marchenko.artauction.artwork.repository.ArtworkRepository
import ua.marchenko.artauction.user.enums.Role
import ua.marchenko.artauction.user.service.UserService
import kotlin.test.Test
import getRandomEmail
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertFalse
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test
import reactor.kotlin.test.verifyError
import ua.marchenko.artauction.artwork.model.projection.ArtworkFull
import ua.marchenko.artauction.user.model.MongoUser
import user.random

class ArtworkServiceTest {

    @MockK
    private lateinit var mockArtworkRepository: ArtworkRepository

    @MockK
    private lateinit var mockUserService: UserService

    @MockK
    private lateinit var mockAuthentication: Authentication

    @MockK
    private lateinit var mockSecurityContext: SecurityContext

    @InjectMockKs
    private lateinit var artworkService: ArtworkServiceImpl

    @Test
    fun `should return a list of artworks when artworks are present`() {
        // GIVEN
        val artworks = listOf(MongoArtwork.random())
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
        val artwork = MongoArtwork.random(id = id)

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
            .verifyError(ArtworkNotFoundException::class)
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
    fun `should set status and artist before calling repository method`() {
        // GIVEN
        val email = getRandomEmail()
        val user = MongoUser.random(email = email, role = Role.ARTIST)
        val artworkToSave = MongoArtwork.random(status = null, artistId = null)
        val expectedArtwork = artworkToSave.copy(status = ArtworkStatus.VIEW, artistId = user.id)

        every { mockAuthentication.name } returns email
        every { mockSecurityContext.authentication } returns mockAuthentication
        every { mockUserService.getByEmail(email) } returns user.toMono()
        every { mockArtworkRepository.save(expectedArtwork) } returns expectedArtwork.toMono()

        // WHEN
        val result = artworkService.save(artworkToSave)
            .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(mockSecurityContext.toMono()))

        // THEN
        result.test()
            .expectNext(expectedArtwork)
            .verifyComplete()
        verify { mockArtworkRepository.save(expectedArtwork) }
    }

    @Test
    fun `should return false when there is no artwork with given id`() {
        //GIVEN
        every { mockArtworkRepository.existsById(any()) } returns false.toMono()

        //WHEN
        val result = artworkService.existsById(ObjectId().toHexString())

        //THEN
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
