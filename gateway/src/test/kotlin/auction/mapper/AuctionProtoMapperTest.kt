package auction.mapper

import ua.marchenko.artauction.auction.AuctionProtoFixture
import ua.marchenko.artauction.auction.AuctionProtoFixture.randomCreateAuctionRequestProtoGrpc
import ua.marchenko.artauction.auction.AuctionProtoFixture.randomSuccessCreateAuctionResponseProto
import ua.marchenko.artauction.auction.AuctionProtoFixture.randomSuccessFindByIdResponseProto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import ua.marchenko.artauction.getRandomString
import ua.marchenko.core.auction.exception.AuctionNotFoundException
import ua.marchenko.core.auction.exception.InvalidAuctionOperationException
import ua.marchenko.gateway.auction.mapper.toAuctionProtoList
import ua.marchenko.gateway.auction.mapper.toCreateAuctionRequestProtoInternal
import ua.marchenko.gateway.auction.mapper.toCreateAuctionResponseProtoGrpc
import ua.marchenko.gateway.auction.mapper.toFindAuctionByIdRequestProtoInternal
import ua.marchenko.gateway.auction.mapper.toFindAuctionByIdResponseProtoGrpc
import ua.marchenko.internal.input.reqreply.auction.FindAuctionByIdResponse as FindAuctionByIdResponseProtoInternal
import ua.marchenko.internal.input.reqreply.auction.FindAuctionByIdRequest as FindAuctionByIdRequestProtoInternal
import ua.marchenko.internal.input.reqreply.auction.CreateAuctionResponse as CreateAuctionResponseProtoInternal
import ua.marchenko.internal.input.reqreply.auction.CreateAuctionRequest as CreateArtworkRequestProtoInternal
import ua.marchenko.grpcapi.input.reqreply.auction.FindAuctionByIdRequest as FindAuctionByIdRequestProtoGrpc
import ua.marchenko.grpcapi.input.reqreply.auction.CreateAuctionResponse as CreateAuctionResponseProtoGrpc
import ua.marchenko.grpcapi.input.reqreply.auction.FindAuctionByIdResponse as FindAuctionByIdResponseProtoGrpc
import ua.marchenko.internal.input.reqreply.auction.FindAllAuctionsResponse as FindAllAuctionsResponseProtoInternal

class AuctionProtoMapperTest {

    @Test
    fun `should build FindAuctionByIdRequestProtoInternal from FindAuctionByIdRequestProtoGrpc`() {
        // GIVEN
        val id = getRandomString()
        val request = FindAuctionByIdRequestProtoGrpc.newBuilder().setId(id).build()
        val expectedResponse = FindAuctionByIdRequestProtoInternal.newBuilder().setId(id).build()

        // WHEN
        val result = request.toFindAuctionByIdRequestProtoInternal()

        // THEN
        assertEquals(expectedResponse, result)
    }

    @Test
    fun `should build FindAuctionByIdResponseGrpc success when FindAuctionByIdResponseInternal is success`() {
        // GIVEN
        val internalResponse = randomSuccessFindByIdResponseProto()
        val expectedResponse = FindAuctionByIdResponseProtoGrpc.newBuilder().apply {
            successBuilder.auction = internalResponse.success.auction
        }.build()

        // WHEN
        val result = internalResponse.toFindAuctionByIdResponseProtoGrpc()

        // THEN
        assertEquals(expectedResponse, result)
    }

    @Test
    fun `should throw exception when the case in FindAuctionByIdResponseProtoInternal is not set`() {
        // GIVEN
        val internalResponse = FindAuctionByIdResponseProtoInternal.newBuilder().build()

        // WHEN THEN
        val exception = assertThrows<IllegalStateException> { internalResponse.toFindAuctionByIdResponseProtoGrpc() }
        assertEquals(RESPONSE_NOT_SET_ERROR_MESSAGE, exception.message)
    }

    @ParameterizedTest
    @MethodSource("findAuctionByIdResponseProtoInternalFailureData")
    fun `should throw exception when the case in FindAuctionByIdResponseProtoInternal is failure`(
        response: FindAuctionByIdResponseProtoInternal,
        ex: Throwable
    ) {
        // WHEN THEN
        val exception = assertThrows<Throwable> { response.toFindAuctionByIdResponseProtoGrpc() }
        assertTrue(ex::class.isInstance(exception), "Unexpected exception type thrown")
        assertEquals(ex.message, exception.message)
    }

    @Test
    fun `should build CreateAuctionRequestProtoInternal from CreateAuctionRequestProtoGrpc`() {
        // GIVEN
        val request = randomCreateAuctionRequestProtoGrpc()
        val expectedRequest = CreateArtworkRequestProtoInternal.newBuilder().apply {
            artworkId = request.artworkId
            startBid = request.startBid
            startedAt = request.startedAt
            finishedAt = request.finishedAt
        }.build()

        // WHEN
        val result = request.toCreateAuctionRequestProtoInternal()

        // THEN
        assertEquals(expectedRequest, result)
    }

    @Test
    fun `should build CreateAuctionResponseProtoGrpc success when CreateAuctionResponseProtoInternal is success`() {
        // GIVEN
        val internalResponse = randomSuccessCreateAuctionResponseProto()
        val expectedResponse = CreateAuctionResponseProtoGrpc.newBuilder().apply {
            successBuilder.auction = internalResponse.success.auction
        }.build()

        // WHEN
        val result = internalResponse.toCreateAuctionResponseProtoGrpc()

        // THEN
        assertEquals(expectedResponse, result)
    }

    @ParameterizedTest
    @MethodSource("createAuctionResponseProtoInternalFailureData")
    fun `should throw exception when the case in CreateAuctionResponseProtoInternal is failure`(
        response: CreateAuctionResponseProtoInternal,
        ex: Throwable
    ) {
        // WHEN THEN
        val exception = assertThrows<Throwable> { response.toCreateAuctionResponseProtoGrpc() }
        assertTrue(ex::class.isInstance(exception), "Unexpected exception type thrown")
        assertEquals(ex.message, exception.message)
    }

    @Test
    fun `should throw exception when the case in CreateAuctionResponseProtoInternal is not set`() {
        // GIVEN
        val internalResponse = CreateAuctionResponseProtoInternal.newBuilder().build()

        // WHEN THEN
        val exception = assertThrows<IllegalStateException> { internalResponse.toCreateAuctionResponseProtoGrpc() }
        assertEquals(RESPONSE_NOT_SET_ERROR_MESSAGE, exception.message)
    }

    @Test
    fun `should throw exception when the case in FindAllAuctionsResponseProtoInternal is not set`() {
        // GIVEN
        val internalResponse = FindAllAuctionsResponseProtoInternal.newBuilder().build()

        // WHEN THEN
        val exception = assertThrows<IllegalStateException> { internalResponse.toAuctionProtoList() }
        assertEquals(RESPONSE_NOT_SET_ERROR_MESSAGE, exception.message)
    }

    companion object {
        private const val RESPONSE_NOT_SET_ERROR_MESSAGE = "Response not set"

        @JvmStatic
        fun findAuctionByIdResponseProtoInternalFailureData(): List<Arguments> = listOf(
            Arguments.of(
                AuctionProtoFixture.randomFailureGeneralFindAuctionByIdResponseProtoInternal(),
                IllegalStateException(AuctionProtoFixture.ERROR_MESSAGE)
            ),
            Arguments.of(
                AuctionProtoFixture.randomFailureAuctionNotFoundFindAuctionByIdResponseProtoInternal(),
                AuctionNotFoundException(message = AuctionProtoFixture.ERROR_MESSAGE)
            )
        )

        @JvmStatic
        fun createAuctionResponseProtoInternalFailureData(): List<Arguments> = listOf(
            Arguments.of(
                AuctionProtoFixture.randomFailureGeneralCreateAuctionResponseProtoInternal(),
                IllegalStateException(AuctionProtoFixture.ERROR_MESSAGE)
            ),
            Arguments.of(
                AuctionProtoFixture.randomFailureInvalidAuctionOperationCreateAuctionResponseProtoInternal(),
                InvalidAuctionOperationException(message = AuctionProtoFixture.ERROR_MESSAGE)
            )
        )
    }
}
