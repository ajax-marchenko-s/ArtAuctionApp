package ua.marchenko.artauction.artwork.controller

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import ua.marchenko.artauction.artwork.exception.ArtworkNotFoundException
import ua.marchenko.artauction.artwork.mapper.toArtwork
import ua.marchenko.artauction.artwork.mapper.toArtworkResponse
import ua.marchenko.artauction.artwork.service.ArtworkService
import ua.marchenko.artauction.common.artwork.getRandomArtwork
import ua.marchenko.artauction.common.artwork.getRandomArtworkRequest
import kotlin.test.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class ArtworkControllerTest {

    private val mockArtworkService: ArtworkService = mock(ArtworkService::class.java)
    private val artworkController: ArtworkController = ArtworkController(mockArtworkService)

    @Test
    fun `getAllArtworks should return a list of ArtworkResponse`() {
        val artworks = listOf(getRandomArtwork())
        `when`(mockArtworkService.getAll()).thenReturn(artworks)
        val result = artworkController.getAllArtworks()
        assertEquals(1, result.size)
        assertEquals(artworks[0].toArtworkResponse(), result[0])
    }

    @Test
    fun `getAllArtworks should return an empty list if there are no artworks`() {
        `when`(mockArtworkService.getAll()).thenReturn(listOf())
        val result = artworkController.getAllArtworks()
        assertEquals(0, result.size)
    }

    @Test
    fun `getArtworkById should return artwork by id if artwork with this id exists`() {
        val id = "1"
        val artwork = getRandomArtwork(id = id)
        `when`(mockArtworkService.getById(id)).thenReturn(artwork)
        val result = artworkController.getArtworkById(id)
        assertEquals(artwork.toArtworkResponse(), result)
    }

    @Test
    fun `getArtworkById should throw ArtworkNotFoundException if there is no artwork with this id`() {
        val id = "1"
        `when`(mockArtworkService.getById(id)).thenThrow(ArtworkNotFoundException(id))
        assertThrows<ArtworkNotFoundException> { artworkController.getArtworkById(id) }
    }

    @Test
    fun `save should return ArtworkResponse`() {
        val artworkRequest = getRandomArtworkRequest()
        val artwork = getRandomArtwork()
        `when`(mockArtworkService.save(artworkRequest.toArtwork())).thenReturn(artwork)
        val result = artworkController.addArtwork(artworkRequest)
        assertEquals(artwork.toArtworkResponse(), result)
    }

}
