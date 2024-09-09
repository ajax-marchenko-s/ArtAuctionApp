@file:Suppress("VarCouldBeVal")
package ua.marchenko.artauction.artwork.controller

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import ua.marchenko.artauction.artwork.exception.ArtworkNotFoundException
import ua.marchenko.artauction.artwork.mapper.toArtwork
import ua.marchenko.artauction.artwork.mapper.toArtworkResponse
import ua.marchenko.artauction.artwork.service.ArtworkService
import artwork.random
import getRandomObjectId
import kotlin.test.Test
import getRandomString
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import ua.marchenko.artauction.artwork.controller.dto.CreateArtworkRequest
import ua.marchenko.artauction.artwork.model.Artwork

class ArtworkControllerTest {

    @MockK
    private lateinit var mockArtworkService: ArtworkService

    @InjectMockKs
    private lateinit var artworkController: ArtworkController

    @Test
    fun `should return a list of ArtworkResponse when there are some artworks`() {
        // GIVEN
        val artworks = listOf(Artwork.random())
        every { mockArtworkService.getAll() } returns artworks

        // WHEN
        val result = artworkController.getAllArtworks()

        //THEN
        assertEquals(1, result.size)
        assertEquals(artworks[0].toArtworkResponse(), result[0])
    }

    @Test
    fun `should return an empty list when there are no artworks`() {
        // GIVEN
        every { mockArtworkService.getAll() } returns emptyList()

        // WHEN
        val result = artworkController.getAllArtworks()

        //THEN
        assertEquals(0, result.size)
    }

    @Test
    fun `should return artwork by id when artwork with this id exists`() {
        // GIVEN
        val id = getRandomObjectId().toHexString()
        val artwork = Artwork.random(id = id)

        every { mockArtworkService.getById(id) } returns artwork

        // WHEN
        val result = artworkController.getArtworkById(id)

        //THEN
        assertEquals(artwork.toArtworkResponse(), result)
    }

    @Test
    fun `should throw ArtworkNotFoundException when there is no artwork with this id`() {
        // GIVEN
        val id = getRandomString()
        every { mockArtworkService.getById(id) } throws ArtworkNotFoundException(id)

        // WHEN-THEN
        assertThrows<ArtworkNotFoundException> { artworkController.getArtworkById(id) }
    }

    @Test
    fun `should return ArtworkResponse with data from request`() {
        // GIVEN
        val artworkRequest = CreateArtworkRequest.random()
        val artwork = Artwork.random()

        every { mockArtworkService.save(artworkRequest.toArtwork()) } returns artwork

        // WHEN
        val result = artworkController.addArtwork(artworkRequest)

        //THEN
        assertEquals(artwork.toArtworkResponse(), result)
    }
}
