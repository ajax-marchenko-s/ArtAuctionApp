package ua.marchenko.artauction.domainservice.user.infrastructure.nats.mapper

import kotlin.test.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.Arguments
import ua.marchenko.artauction.domainservice.artwork.domain.random
import ua.marchenko.artauction.domainservice.artwork.domain.Artwork
import ua.marchenko.artauction.domainservice.artwork.domain.Artwork.ArtworkStyle
import ua.marchenko.artauction.domainservice.artwork.domain.Artwork.ArtworkStatus
import ua.marchenko.artauction.domainservice.artwork.infrastructure.nats.mapper.toArtworkProto
import ua.marchenko.artauction.domainservice.artwork.infrastructure.nats.mapper.toArtworkStatusProto
import ua.marchenko.artauction.domainservice.artwork.infrastructure.nats.mapper.toArtworkStyle
import ua.marchenko.artauction.domainservice.artwork.infrastructure.nats.mapper.toArtworkStyleProto
import ua.marchenko.artauction.domainservice.artwork.infrastructure.nats.mapper.toFindAllArtworksFailureResponseProto
import ua.marchenko.artauction.domainservice.artwork.infrastructure.nats.mapper.toFindAllArtworksFullFailureResponseProto
import ua.marchenko.internal.commonmodels.artwork.Artwork as ArtworkProto
import ua.marchenko.internal.commonmodels.artwork.Artwork.ArtworkStyle as ArtworkStyleProto
import ua.marchenko.internal.commonmodels.artwork.Artwork.ArtworkStatus as ArtworkStatusProto
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksFullResponse as FindAllArtworksFullResponseProto
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksResponse as FindAllArtworksResponseProto

class ArtworkMapperProtoTest {

    @Test
    fun `should build FindAllArtworksResponse with failure when called on throwable`() {
        // GIVEN
        val error = Throwable(ERROR_MESSAGE)
        val expectedResponse = FindAllArtworksResponseProto.newBuilder().also { builder ->
            builder.failureBuilder.message = ERROR_MESSAGE
        }.build()

        // WHEN
        val result = error.toFindAllArtworksFailureResponseProto()

        //THEN
        assertEquals(expectedResponse, result)
    }

    @Test
    fun `should build FindAllArtworksFullResponse with failure when called on throwable`() {
        // GIVEN
        val error = Throwable(ERROR_MESSAGE)
        val expectedResponse = FindAllArtworksFullResponseProto.newBuilder().also { builder ->
            builder.failureBuilder.message = ERROR_MESSAGE
        }.build()

        // WHEN
        val result = error.toFindAllArtworksFullFailureResponseProto()

        //THEN
        assertEquals(expectedResponse, result)
    }

    @Test
    fun `should return ArtworkProto when Artwork has all non-null properties`() {
        // GIVEN
        val artwork = Artwork.random()
        val expectedArtworkProto = ArtworkProto.newBuilder().also {
            it.id = artwork.id!!
            it.title = artwork.title
            it.description = artwork.description
            it.width = artwork.width
            it.height = artwork.height
            it.style = artwork.style.toArtworkStyleProto()
            it.status = artwork.status.toArtworkStatusProto()
            it.artistId = artwork.artistId
        }.build()

        // WHEN
        val result = artwork.toArtworkProto()

        //THEN
        assertEquals(expectedArtworkProto, result)
    }

    @ParameterizedTest
    @MethodSource("artworkStyleProtoToArtworkStyleData")
    fun `should map ArtworkStyleProto to ArtworkStyle enum values`(
        valueFrom: ArtworkStyleProto,
        valueTo: ArtworkStyle,
    ) {
        // WHEN THEN
        assertEquals(valueTo, valueFrom.toArtworkStyle())
    }

    @Test
    fun `should throw exception when mapping ArtworkStyleProto_ARTWORK_STYLE_UNSPECIFIED to ArtworkStyle`() {
        // GIVEN
        val artworkStyleProto = ArtworkStyleProto.ARTWORK_STYLE_UNSPECIFIED

        // WHEN THEN
        assertThrows<IllegalArgumentException> { artworkStyleProto.toArtworkStyle() }
    }

    @ParameterizedTest
    @MethodSource("artworkStyleToArtworkStyleProtoData")
    fun `should map ArtworkStyle to ArtworkStyleProto enum values`(
        valueFrom: ArtworkStyle,
        valueTo: ArtworkStyleProto,
    ) {
        // WHEN THEN
        assertEquals(valueTo, valueFrom.toArtworkStyleProto())
    }

    @ParameterizedTest
    @MethodSource("artworkStatusToArtworkStatusProtoData")
    fun `should map ArtworkStatus to ArtworkStatusProto enum values`(
        valueFrom: ArtworkStatus,
        valueTo: ArtworkStatusProto,
    ) {
        // WHEN THEN
        assertEquals(valueTo, valueFrom.toArtworkStatusProto())
    }

    companion object {
        private const val ERROR_MESSAGE = "error message"

        @JvmStatic
        fun artworkStyleProtoToArtworkStyleData(): List<Arguments> = listOf(
            Arguments.of(ArtworkStyleProto.UNRECOGNIZED, ArtworkStyle.UNKNOWN),
            Arguments.of(ArtworkStyleProto.ARTWORK_STYLE_REALISM, ArtworkStyle.REALISM),
            Arguments.of(ArtworkStyleProto.ARTWORK_STYLE_IMPRESSIONISM, ArtworkStyle.IMPRESSIONISM),
            Arguments.of(ArtworkStyleProto.ARTWORK_STYLE_EXPRESSIONISM, ArtworkStyle.EXPRESSIONISM),
            Arguments.of(ArtworkStyleProto.ARTWORK_STYLE_CUBISM, ArtworkStyle.CUBISM),
            Arguments.of(ArtworkStyleProto.ARTWORK_STYLE_SURREALISM, ArtworkStyle.SURREALISM),
            Arguments.of(ArtworkStyleProto.ARTWORK_STYLE_ABSTRACT, ArtworkStyle.ABSTRACT),
            Arguments.of(ArtworkStyleProto.ARTWORK_STYLE_POP_ART, ArtworkStyle.POP_ART),
            Arguments.of(ArtworkStyleProto.ARTWORK_STYLE_MINIMALISM, ArtworkStyle.MINIMALISM),
            Arguments.of(ArtworkStyleProto.ARTWORK_STYLE_RENAISSANCE, ArtworkStyle.RENAISSANCE)
        )

        @JvmStatic
        fun artworkStyleToArtworkStyleProtoData(): List<Arguments> = listOf(
            Arguments.of(ArtworkStyle.UNKNOWN, ArtworkStyleProto.UNRECOGNIZED),
            Arguments.of(ArtworkStyle.REALISM, ArtworkStyleProto.ARTWORK_STYLE_REALISM),
            Arguments.of(ArtworkStyle.IMPRESSIONISM, ArtworkStyleProto.ARTWORK_STYLE_IMPRESSIONISM),
            Arguments.of(ArtworkStyle.EXPRESSIONISM, ArtworkStyleProto.ARTWORK_STYLE_EXPRESSIONISM),
            Arguments.of(ArtworkStyle.CUBISM, ArtworkStyleProto.ARTWORK_STYLE_CUBISM),
            Arguments.of(ArtworkStyle.SURREALISM, ArtworkStyleProto.ARTWORK_STYLE_SURREALISM),
            Arguments.of(ArtworkStyle.ABSTRACT, ArtworkStyleProto.ARTWORK_STYLE_ABSTRACT),
            Arguments.of(ArtworkStyle.POP_ART, ArtworkStyleProto.ARTWORK_STYLE_POP_ART),
            Arguments.of(ArtworkStyle.MINIMALISM, ArtworkStyleProto.ARTWORK_STYLE_MINIMALISM),
            Arguments.of(ArtworkStyle.RENAISSANCE, ArtworkStyleProto.ARTWORK_STYLE_RENAISSANCE)
        )

        @JvmStatic
        fun artworkStatusToArtworkStatusProtoData(): List<Arguments> = listOf(
            Arguments.of(ArtworkStatus.UNKNOWN, ArtworkStatusProto.UNRECOGNIZED),
            Arguments.of(ArtworkStatus.SOLD, ArtworkStatusProto.ARTWORK_STATUS_SOLD),
            Arguments.of(ArtworkStatus.ON_AUCTION, ArtworkStatusProto.ARTWORK_STATUS_ON_AUCTION),
            Arguments.of(ArtworkStatus.VIEW, ArtworkStatusProto.ARTWORK_STATUS_VIEW),
        )
    }
}
