//package ua.marchenko.artauction.artwork.controller
//
//import org.junit.jupiter.api.Assertions.assertEquals
//import org.junit.jupiter.api.assertThrows
//import ua.marchenko.artauction.artwork.exception.ArtworkNotFoundException
//import ua.marchenko.artauction.artwork.service.ArtworkService
//import artwork.random
//import kotlin.test.Test
//import getRandomString
//import io.mockk.every
//import io.mockk.impl.annotations.InjectMockKs
//import io.mockk.impl.annotations.MockK
//import org.bson.types.ObjectId
//import ua.marchenko.artauction.artwork.controller.dto.CreateArtworkRequest
//import ua.marchenko.artauction.artwork.mapper.toMongo
//import ua.marchenko.artauction.artwork.mapper.toResponse
//import ua.marchenko.artauction.artwork.model.MongoArtwork
//
//class ArtworkControllerTest {
//
//    @MockK
//    private lateinit var mockArtworkService: ArtworkService
//
//    @InjectMockKs
//    private lateinit var artworkController: ArtworkController
//
//    @Test
//    fun `should return a list of ArtworkResponse when there are some artworks`() {
//        // GIVEN
//        val artworks = listOf(MongoArtwork.random())
//        every { mockArtworkService.getAll() } returns artworks
//
//        // WHEN
//        val result = artworkController.getAllArtworks(0, 10)
//
//        //THEN
//        assertEquals(1, result.size)
//        assertEquals(artworks[0].toResponse(), result[0])
//    }
//
//    @Test
//    fun `should return an empty list when there are no artworks`() {
//        // GIVEN
//        every { mockArtworkService.getAll() } returns emptyList()
//
//        // WHEN
//        val result = artworkController.getAllArtworks(0, 10)
//
//        //THEN
//        assertEquals(0, result.size)
//    }
//
//    @Test
//    fun `should return artwork by id when artwork with this id exists`() {
//        // GIVEN
//        val id = ObjectId().toHexString()
//        val artwork = MongoArtwork.random(id = id)
//
//        every { mockArtworkService.getById(id) } returns artwork
//
//        // WHEN
//        val result = artworkController.getArtworkById(id)
//
//        //THEN
//        assertEquals(artwork.toResponse(), result)
//    }
//
//    @Test
//    fun `should throw ArtworkNotFoundException when there is no artwork with this id`() {
//        // GIVEN
//        val id = getRandomString()
//        every { mockArtworkService.getById(id) } throws ArtworkNotFoundException(id)
//
//        // WHEN-THEN
//        assertThrows<ArtworkNotFoundException> { artworkController.getArtworkById(id) }
//    }
//
//    @Test
//    fun `should return ArtworkResponse with data from request`() {
//        // GIVEN
//        val artworkRequest = CreateArtworkRequest.random()
//        val artwork = MongoArtwork.random()
//
//        every { mockArtworkService.save(artworkRequest.toMongo()) } returns artwork
//
//        // WHEN
//        val result = artworkController.addArtwork(artworkRequest)
//
//        //THEN
//        assertEquals(artwork.toResponse(), result)
//    }
//}
