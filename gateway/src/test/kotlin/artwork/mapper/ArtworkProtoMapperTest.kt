package artwork.mapper

import artwork.ArtworkProtoFixture
import artwork.random
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ua.marchenko.core.artwork.dto.ArtworkFullResponse
import ua.marchenko.core.artwork.enums.ArtworkStatus
import ua.marchenko.core.artwork.enums.ArtworkStyle
import ua.marchenko.gateway.artwork.controller.dto.ArtworkResponse
import ua.marchenko.gateway.artwork.controller.dto.CreateArtworkRequest
import ua.marchenko.gateway.artwork.mapper.toArtworkFullResponse
import ua.marchenko.gateway.artwork.mapper.toArtworkResponse
import ua.marchenko.gateway.artwork.mapper.toArtworksList
import ua.marchenko.gateway.artwork.mapper.toCreateArtworkRequestProto
import ua.marchenko.gateway.artwork.mapper.toFullArtworkList
import ua.marchenko.gateway.user.toUserResponse
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

    @Test
    fun `should throw IllegalStateException when the case in CreateArtworkResponse is failure`() {
        // GIVEN
        val response = ArtworkProtoFixture.randomFailureCreateArtworkResponseProto()

        // WHEN THEN
        val exception = assertThrows<IllegalStateException> { response.toArtworkResponse() }
        assertEquals(ArtworkProtoFixture.ERROR_MESSAGE, exception.message)
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
}
