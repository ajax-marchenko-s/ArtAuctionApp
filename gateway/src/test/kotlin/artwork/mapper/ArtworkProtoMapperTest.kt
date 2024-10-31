package artwork.mapper

import artwork.ArtworkProtoFixture
import artwork.random
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import ua.marchenko.core.artwork.dto.ArtworkFullResponse
import ua.marchenko.core.artwork.enums.ArtworkStatus
import ua.marchenko.core.artwork.enums.ArtworkStyle
import ua.marchenko.core.user.exception.UserNotFoundException
import ua.marchenko.gateway.artwork.controller.dto.ArtworkResponse
import ua.marchenko.gateway.artwork.controller.dto.CreateArtworkRequest
import ua.marchenko.gateway.artwork.mapper.toArtworkFullResponse
import ua.marchenko.gateway.artwork.mapper.toArtworkResponse
import ua.marchenko.gateway.artwork.mapper.toArtworkStatus
import ua.marchenko.gateway.artwork.mapper.toArtworkStyle
import ua.marchenko.gateway.artwork.mapper.toArtworkStyleProto
import ua.marchenko.gateway.artwork.mapper.toArtworksList
import ua.marchenko.gateway.artwork.mapper.toCreateArtworkRequestProto
import ua.marchenko.gateway.artwork.mapper.toFullArtworkList
import ua.marchenko.gateway.user.toUserResponse
import ua.marchenko.internal.input.reqreply.artwork.CreateArtworkResponse
import user.UserProtoFixture
import ua.marchenko.internal.input.reqreply.artwork.CreateArtworkRequest as CreateArtworkRequestProto
import ua.marchenko.internal.commonmodels.artwork.ArtworkStyle as ArtworkStyleProto
import ua.marchenko.internal.commonmodels.artwork.ArtworkStatus as ArtworkStatusProto

class ArtworkProtoMapperTest {

    @Test
    fun `should build CreateArtworkRequestProto from CreateArtworkRequest`() {
        // GIVEN
        val request = CreateArtworkRequest.random(style = ArtworkStyle.POP_ART)
        val expectedRequestProto = CreateArtworkRequestProto
            .newBuilder()
            .setTitle(request.title)
            .setDescription(request.description)
            .setWidth(request.width)
            .setHeight(request.height)
            .setStyle(ArtworkStyleProto.ARTWORK_STYLE_POP_ART)
            .setArtistId(request.artistId)
            .build()

        // WHEN
        val result = request.toCreateArtworkRequestProto()

        // THEN
        assertEquals(expectedRequestProto, result)
    }

    @Test
    fun `should build ArtworkResponse from CreateArtworkResponseProto when the case is success`() {
        // GIVEN
        val response = ArtworkProtoFixture.randomSuccessCreateArtworkResponseProto(
            style = ArtworkStyleProto.ARTWORK_STYLE_POP_ART,
            status = ArtworkStatusProto.ARTWORK_STATUS_ON_AUCTION,
        )
        val expectedResponse = ArtworkResponse(
            id = response.success.artwork.id,
            title = response.success.artwork.title,
            description = response.success.artwork.description,
            style = ArtworkStyle.POP_ART,
            width = response.success.artwork.width,
            height = response.success.artwork.height,
            status = ArtworkStatus.ON_AUCTION,
            artistId = response.success.artwork.artistId
        )

        // WHEN
        val result = response.toArtworkResponse()

        // THEN
        assertEquals(expectedResponse, result)
    }

    @ParameterizedTest
    @MethodSource("createArtworkResponseFailureData")
    fun `should throw exception when the case in CreateArtworkResponse is failure`(
        response: CreateArtworkResponse,
        ex: Throwable
    ) {
        // WHEN THEN
        val exception = assertThrows<Throwable> { response.toArtworkResponse() }
        assertTrue(ex::class.isInstance(exception), "Unexpected exception type thrown")
        assertEquals(ex.message, exception.message)
    }

    @Test
    fun `should build ArtworkResponse from FindArtworkByIdResponseProto when the case is success`() {
        // GIVEN
        val response = ArtworkProtoFixture.randomSuccessFindArtworkByIdResponseProto(
            style = ArtworkStyleProto.ARTWORK_STYLE_SURREALISM,
            status = ArtworkStatusProto.ARTWORK_STATUS_ON_AUCTION,
        )
        val expectedResponse = ArtworkResponse(
            id = response.success.artwork.id,
            title = response.success.artwork.title,
            description = response.success.artwork.description,
            style = ArtworkStyle.SURREALISM,
            width = response.success.artwork.width,
            height = response.success.artwork.height,
            status = ArtworkStatus.ON_AUCTION,
            artistId = response.success.artwork.artistId
        )

        // WHEN
        val result = response.toArtworkResponse()

        // THEN
        assertEquals(expectedResponse, result)
    }

    @Test
    fun `should throw IllegalStateException when the case in FindArtworkByIdResponseProto is not set failure`() {
        // GIVEN
        val response = ArtworkProtoFixture.randomFailureGeneralFindArtworkByIdResponseProto()

        // WHEN THEN
        val exception = assertThrows<IllegalStateException> { response.toArtworkResponse() }
        assertEquals(ArtworkProtoFixture.ERROR_MESSAGE, exception.message)
    }

    @Test
    fun `should build ArtworkFullResponse from FindArtworkByIdFullResponseProto when the case is success`() {
        // GIVEN
        val artist = UserProtoFixture.randomUserProto()
        val response = ArtworkProtoFixture.randomSuccessFindArtworkFullByIdResponseProto(
            style = ArtworkStyleProto.ARTWORK_STYLE_REALISM,
            status = ArtworkStatusProto.ARTWORK_STATUS_ON_AUCTION,
            artist = artist
        )
        val expectedResponse = ArtworkFullResponse(
            id = response.success.artwork.id,
            title = response.success.artwork.title,
            description = response.success.artwork.description,
            style = ArtworkStyle.REALISM,
            width = response.success.artwork.width,
            height = response.success.artwork.height,
            status = ArtworkStatus.ON_AUCTION,
            artist = artist.toUserResponse()
        )

        // WHEN
        val result = response.toArtworkFullResponse()

        // THEN
        assertEquals(expectedResponse, result)
    }

    @Test
    fun `should throw IllegalStateException when the case in FindArtworkByIdFullResponseProto is not set failure`() {
        // GIVEN
        val response = ArtworkProtoFixture.randomFailureGeneralFindArtworkFullByIdResponseProto()

        // WHEN THEN
        val exception = assertThrows<IllegalStateException> { response.toArtworkFullResponse() }
        assertEquals(ArtworkProtoFixture.ERROR_MESSAGE, exception.message)
    }

    @Test
    fun `should throw IllegalStateException when the case in FindAllArtworkResponseProto is not set failure`() {
        // GIVEN
        val response = ArtworkProtoFixture.randomFailureGeneralFindAllArtworkResponseProto()

        // WHEN THEN
        val exception = assertThrows<IllegalStateException> { response.toArtworksList() }
        assertEquals(ArtworkProtoFixture.ERROR_MESSAGE, exception.message)
    }

    @Test
    fun `should throw IllegalStateException when the case in FindAllArtworkFullResponseProto is not set failure`() {
        // GIVEN
        val response = ArtworkProtoFixture.randomFailureGeneralFindAllArtworkFullResponseProto()

        // WHEN THEN
        val exception = assertThrows<IllegalStateException> { response.toFullArtworkList() }
        assertEquals(ArtworkProtoFixture.ERROR_MESSAGE, exception.message)
    }

    @ParameterizedTest
    @MethodSource("artworkStatusProtoToArtworkStatusData")
    fun `should map ArtworkStatusProto to ArtworkStatus enum values`(
        valueFrom: ArtworkStatusProto,
        valueTo: ArtworkStatus,
    ) {
        // WHEN THEN
        assertEquals(valueTo, valueFrom.toArtworkStatus())
    }

    @Test
    fun `should throw exception when mapping ArtworkStatusProto_ARTWORK_STATUS_UNSPECIFIED to ArtworkStatus`() {
        // GIVEN
        val artworkStatusProto = ArtworkStatusProto.ARTWORK_STATUS_UNSPECIFIED

        // WHEN THEN
        assertThrows<IllegalArgumentException> { artworkStatusProto.toArtworkStatus() }
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

    companion object {
        @JvmStatic
        fun artworkStatusProtoToArtworkStatusData(): List<Arguments> =
            listOf(
                Arguments.of(ArtworkStatusProto.ARTWORK_STATUS_SOLD, ArtworkStatus.SOLD),
                Arguments.of(ArtworkStatusProto.ARTWORK_STATUS_VIEW, ArtworkStatus.VIEW),
                Arguments.of(ArtworkStatusProto.ARTWORK_STATUS_ON_AUCTION, ArtworkStatus.ON_AUCTION),
                Arguments.of(ArtworkStatusProto.UNRECOGNIZED, ArtworkStatus.UNKNOWN)
            )

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
        fun createArtworkResponseFailureData(): List<Arguments> = listOf(
            Arguments.of(
                ArtworkProtoFixture.randomFailureGeneralCreateArtworkResponseProto(),
                IllegalStateException(ArtworkProtoFixture.ERROR_MESSAGE)
            ),
            Arguments.of(
                ArtworkProtoFixture.randomFailureUserNotFoundCreateArtworkResponseProto(),
                UserNotFoundException(value = ArtworkProtoFixture.ERROR_MESSAGE)
            )
        )
    }
}
