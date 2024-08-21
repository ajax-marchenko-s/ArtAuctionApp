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
import org.mockito.Mockito.mock
import getRandomString
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.whenever
import ua.marchenko.artauction.artwork.controller.dto.CreateArtworkRequest
import ua.marchenko.artauction.artwork.model.Artwork

class ArtworkControllerTest {

    private val mockArtworkService: ArtworkService = mock(ArtworkService::class.java)
    private val artworkController: ArtworkController = ArtworkController(mockArtworkService)

    @Test
    fun `getAllArtworks should return a list of ArtworkResponse`() {
        // GIVEN
        val artworks = listOf(Artwork.random())
        whenever(mockArtworkService.getAll()) doReturn (artworks)

        // WHEN
        val result = artworkController.getAllArtworks()

        //THEN
        assertEquals(1, result.size)
        assertEquals(artworks[0].toArtworkResponse(), result[0])
    }

    @Test
    fun `getAllArtworks should return an empty list if there are no artworks`() {
        // GIVEN
        whenever(mockArtworkService.getAll()) doReturn (listOf())

        // WHEN
        val result = artworkController.getAllArtworks()

        //THEN
        assertEquals(0, result.size)
    }

    @Test
    fun `getArtworkById should return artwork by id if artwork with this id exists`() {
        // GIVEN
        val id = getRandomObjectId().toString()
        val artwork = Artwork.random(id = id)

        whenever(mockArtworkService.getById(id)) doReturn (artwork)

        // WHEN
        val result = artworkController.getArtworkById(id)

        //THEN
        assertEquals(artwork.toArtworkResponse(), result)
    }

    @Test
    fun `getArtworkById should throw ArtworkNotFoundException if there is no artwork with this id`() {
        // GIVEN
        val id = getRandomString()
        whenever(mockArtworkService.getById(id)) doThrow (ArtworkNotFoundException(id))

        // WHEN-THEN
        assertThrows<ArtworkNotFoundException> { artworkController.getArtworkById(id) }
    }

    @Test
    fun `save should return ArtworkResponse`() {
        // GIVEN
        val artworkRequest = CreateArtworkRequest.random()
        val artwork = Artwork.random()

        whenever(mockArtworkService.save(artworkRequest.toArtwork())) doReturn (artwork)

        // WHEN
        val result = artworkController.addArtwork(artworkRequest)

        //THEN
        assertEquals(artwork.toArtworkResponse(), result)
    }
}
