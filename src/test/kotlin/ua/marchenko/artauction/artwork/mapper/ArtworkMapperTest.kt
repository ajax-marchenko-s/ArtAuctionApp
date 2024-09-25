package ua.marchenko.artauction.artwork.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import ua.marchenko.artauction.artwork.controller.dto.ArtworkResponse
import ua.marchenko.artauction.artwork.model.MongoArtwork
import artwork.random
import kotlin.test.Test
import org.bson.types.ObjectId
import org.junit.jupiter.api.assertThrows
import ua.marchenko.artauction.artwork.controller.dto.ArtworkFullResponse
import ua.marchenko.artauction.artwork.controller.dto.CreateArtworkRequest
import ua.marchenko.artauction.artwork.enums.ArtworkStatus
import ua.marchenko.artauction.artwork.model.projection.ArtworkFull
import ua.marchenko.artauction.user.mapper.toResponse

class ArtworkMapperTest {

    @Test
    fun `should return ArtworkResponse when Artwork has not null properties (except fields from business logic)`() {
        // GIVEN
        val mongoArtwork = MongoArtwork.random(artistId = ObjectId().toHexString())
        val expectedArtwork = ArtworkResponse(
            mongoArtwork.id!!.toHexString(),
            mongoArtwork.title!!,
            mongoArtwork.description!!,
            mongoArtwork.style!!,
            mongoArtwork.width!!,
            mongoArtwork.height!!,
            mongoArtwork.status!!,
            mongoArtwork.artistId!!.toHexString()
        )

        //WHEN
        val result = mongoArtwork.toResponse()

        //THEN
        assertEquals(expectedArtwork, result)
    }

    @Test
    fun `should return ArtworkResponse with default values when Artwork has null properties (except fields from bl)`() {
        // GIVEN
        val mongoArtwork = MongoArtwork.random(status = null)
        val expectedArtwork = ArtworkResponse(
            mongoArtwork.id!!.toHexString(),
            mongoArtwork.title!!,
            mongoArtwork.description!!,
            mongoArtwork.style!!,
            mongoArtwork.width!!,
            mongoArtwork.height!!,
            ArtworkStatus.UNKNOWN,
            mongoArtwork.artistId!!.toHexString()
        )

        //WHEN
        val result = mongoArtwork.toResponse()

        //THEN
        assertEquals(expectedArtwork, result)
    }

    @Test
    fun `should throw exception when Artwork id is null`() {
        // GIVEN
        val mongoArtwork = MongoArtwork.random(id = null)

        // WHEN THEN
        val exception = assertThrows<IllegalArgumentException> {
            mongoArtwork.toResponse()
        }
        assertEquals("artwork id cannot be null", exception.message)
    }

    @Test
    fun `should return Artwork`() {
        // GIVEN
        val artwork = CreateArtworkRequest.random()
        val expectedMongoArtwork =
            MongoArtwork(
                null,
                artwork.title,
                artwork.description,
                artwork.style,
                artwork.width,
                artwork.height,
                null,
                null
            )

        //WHEN
        val result = artwork.toMongo()

        //THEN
        assertEquals(expectedMongoArtwork, result)
    }

    @Test
    fun `should return ArtworkFullResponse when ArtworkFull has all non-null properties`() {
        // GIVEN
        val artworkFull = ArtworkFull.random()
        val expectedResponse = ArtworkFullResponse(
            artworkFull.id!!.toHexString(),
            artworkFull.title!!,
            artworkFull.description!!,
            artworkFull.style!!,
            artworkFull.width!!,
            artworkFull.height!!,
            artworkFull.status!!,
            artworkFull.artist!!.toResponse()
        )

        // WHEN
        val result = artworkFull.toFullResponse()

        // THEN
        assertEquals(expectedResponse, result)
    }

    @Test
    fun `should return ArtworkFullResponse with default values when ArtworkFull has null properties`() {
        // GIVEN
        val artworkFull = ArtworkFull.random(status = null)
        val expectedResponse = ArtworkFullResponse(
            artworkFull.id!!.toHexString(),
            artworkFull.title!!,
            artworkFull.description!!,
            artworkFull.style!!,
            artworkFull.width!!,
            artworkFull.height!!,
            ArtworkStatus.UNKNOWN,
            artworkFull.artist!!.toResponse()
        )

        // WHEN
        val result = artworkFull.toFullResponse()

        // THEN
        assertEquals(expectedResponse, result)
    }
}
