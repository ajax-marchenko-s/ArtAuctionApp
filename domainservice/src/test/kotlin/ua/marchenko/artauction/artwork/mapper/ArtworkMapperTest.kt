package ua.marchenko.artauction.artwork.mapper

import artwork.random
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test
import ua.marchenko.artauction.artwork.model.projection.ArtworkFull
import ua.marchenko.artauction.user.mapper.toResponse
import ua.marchenko.core.artwork.dto.ArtworkFullResponse

class ArtworkMapperTest {

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
        val artworkFull = ArtworkFull.random(title = null)
        val expectedResponse = ArtworkFullResponse(
            artworkFull.id!!.toHexString(),
            "unknown",
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
}
