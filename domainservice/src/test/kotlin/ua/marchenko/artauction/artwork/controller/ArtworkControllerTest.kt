package ua.marchenko.artauction.artwork.controller

import ua.marchenko.core.artwork.exception.ArtworkNotFoundException
import ua.marchenko.artauction.artwork.service.ArtworkService
import artwork.random
import kotlin.test.Test
import getRandomString
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.bson.types.ObjectId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test
import ua.marchenko.artauction.artwork.mapper.toMongo
import ua.marchenko.artauction.artwork.mapper.toResponse
import ua.marchenko.artauction.artwork.model.MongoArtwork
import ua.marchenko.core.artwork.dto.CreateArtworkRequest

class ArtworkControllerTest {

    @MockK
    private lateinit var mockArtworkService: ArtworkService

    @InjectMockKs
    private lateinit var artworkController: ArtworkController

    @Test
    fun `should return a list of ArtworkResponse when there are some artworks`() {
        // GIVEN
        val artworks = listOf(MongoArtwork.random())
        every { mockArtworkService.getAll() } returns artworks.toFlux()

        // WHEN
        val result = artworkController.getAllArtworks(0, 10)

        //THEN
        result.test()
            .expectNext(artworks[0].toResponse())
            .verifyComplete()
    }

    @Test
    fun `should return an empty list when there are no artworks`() {
        // GIVEN
        every { mockArtworkService.getAll() } returns Flux.empty()

        // WHEN
        val result = artworkController.getAllArtworks(0, 10)

        //THEN
        result.test()
            .verifyComplete()
    }

    @Test
    fun `should return artwork by id when artwork with this id exists`() {
        // GIVEN
        val id = ObjectId().toHexString()
        val artwork = MongoArtwork.random(id = id)

        every { mockArtworkService.getById(id) } returns artwork.toMono()

        // WHEN
        val result = artworkController.getArtworkById(id)

        // THEN
        result.test()
            .expectNext(artwork.toResponse())
            .verifyComplete()
    }

    @Test
    fun `should throw ArtworkNotFoundException when there is no artwork with this id`() {
        // GIVEN
        val id = getRandomString()
        every { mockArtworkService.getById(id) } returns Mono.error(
            ArtworkNotFoundException(id)
        )

        // WHEN
        val result = artworkController.getArtworkById(id)

        // THEN
        result.test()
            .verifyError(ArtworkNotFoundException::class.java)

    }

    @Test
    fun `should return ArtworkResponse with data from request`() {
        // GIVEN
        val artworkRequest = CreateArtworkRequest.random()
        val artwork = MongoArtwork.random()

        every { mockArtworkService.save(artworkRequest.toMongo()) } returns artwork.toMono()

        // WHEN
        val result = artworkController.addArtwork(artworkRequest)

        //THEN
        result.test()
            .expectNext(artwork.toResponse())
            .verifyComplete()
    }
}
