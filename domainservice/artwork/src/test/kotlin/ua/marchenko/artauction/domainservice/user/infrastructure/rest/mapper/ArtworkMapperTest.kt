package ua.marchenko.artauction.domainservice.user.infrastructure.rest.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test
import ua.marchenko.artauction.domainservice.artwork.domain.random
import ua.marchenko.artauction.domainservice.artwork.domain.projection.ArtworkFull
import ua.marchenko.artauction.domainservice.artwork.infrastructure.rest.dto.ArtworkFullResponse
import ua.marchenko.artauction.domainservice.artwork.infrastructure.rest.mapper.toFullResponse
import ua.marchenko.artauction.domainservice.artwork.infrastructure.rest.mapper.toResponseStatus
import ua.marchenko.artauction.domainservice.artwork.infrastructure.rest.mapper.toResponseStyle

class ArtworkMapperTest {

    @Test
    fun `should return ArtworkFullResponse from ArtworkFull`() {
        // GIVEN
        val artworkFull = ArtworkFull.random()
        val expectedResponse = ArtworkFullResponse(
            artworkFull.id,
            artworkFull.title,
            artworkFull.description,
            artworkFull.style.toResponseStyle(),
            artworkFull.width,
            artworkFull.height,
            artworkFull.status.toResponseStatus(),
            artworkFull.artist.toResponse()
        )

        // WHEN
        val result = artworkFull.toFullResponse()

        // THEN
        assertEquals(expectedResponse, result)
    }
}
