package ua.marchenko.artauction.domainservice.artwork.infrastructure.rest.mapper

import kotlin.test.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import ua.marchenko.artauction.domainservice.artwork.domain.Artwork.ArtworkStatus
import ua.marchenko.artauction.domainservice.artwork.domain.Artwork.ArtworkStyle
import ua.marchenko.artauction.domainservice.artwork.domain.projection.ArtworkFull
import ua.marchenko.artauction.domainservice.artwork.domain.random
import ua.marchenko.artauction.domainservice.artwork.infrastructure.rest.dto.ArtworkFullResponse
import ua.marchenko.artauction.domainservice.artwork.infrastructure.rest.dto.ArtworkFullResponse.ArtworkStyle as ArtworkStyleResponse
import ua.marchenko.artauction.domainservice.artwork.infrastructure.rest.dto.ArtworkFullResponse.ArtworkStatus as ArtworkStatusResponse
import ua.marchenko.artauction.domainservice.user.infrastructure.rest.mapper.toResponse

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

    @ParameterizedTest
    @MethodSource("artworkStyleDomainToArtworkStyleResponseData")
    fun `should map ArtworkStyle to ArtworkStyleResponse enum values`(
        valueFrom: ArtworkStyle,
        valueTo: ArtworkStyleResponse,
    ) {
        // WHEN THEN
        assertEquals(valueTo, valueFrom.toResponseStyle())
    }

    @ParameterizedTest
    @MethodSource("artworkStatusDomainToArtworkStatusResponseData")
    fun `should map ArtworkStatus to ArtworkStatusResponse enum values`(
        valueFrom: ArtworkStatus,
        valueTo: ArtworkStatusResponse,
    ) {
        // WHEN THEN
        assertEquals(valueTo, valueFrom.toResponseStatus())
    }

    companion object {
        @JvmStatic
        fun artworkStyleDomainToArtworkStyleResponseData(): List<Arguments> = listOf(
            Arguments.of(ArtworkStyle.UNKNOWN, ArtworkStyleResponse.UNKNOWN),
            Arguments.of(ArtworkStyle.REALISM, ArtworkStyleResponse.REALISM),
            Arguments.of(ArtworkStyle.IMPRESSIONISM, ArtworkStyleResponse.IMPRESSIONISM),
            Arguments.of(ArtworkStyle.EXPRESSIONISM, ArtworkStyleResponse.EXPRESSIONISM),
            Arguments.of(ArtworkStyle.CUBISM, ArtworkStyleResponse.CUBISM),
            Arguments.of(ArtworkStyle.SURREALISM, ArtworkStyleResponse.SURREALISM),
            Arguments.of(ArtworkStyle.ABSTRACT, ArtworkStyleResponse.ABSTRACT),
            Arguments.of(ArtworkStyle.POP_ART, ArtworkStyleResponse.POP_ART),
            Arguments.of(ArtworkStyle.MINIMALISM, ArtworkStyleResponse.MINIMALISM),
            Arguments.of(ArtworkStyle.RENAISSANCE, ArtworkStyleResponse.RENAISSANCE)
        )

        @JvmStatic
        fun artworkStatusDomainToArtworkStatusResponseData(): List<Arguments> = listOf(
            Arguments.of(ArtworkStatus.ON_AUCTION, ArtworkStatusResponse.ON_AUCTION),
            Arguments.of(ArtworkStatus.SOLD, ArtworkStatusResponse.SOLD),
            Arguments.of(ArtworkStatus.VIEW, ArtworkStatusResponse.VIEW),
            Arguments.of(ArtworkStatus.UNKNOWN, ArtworkStatusResponse.UNKNOWN),
        )
    }
}
