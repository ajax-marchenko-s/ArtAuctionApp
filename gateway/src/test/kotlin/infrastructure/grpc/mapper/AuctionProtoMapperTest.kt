package infrastructure.grpc.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import ua.marchenko.artauction.infrastructure.auction.AuctionProtoFixture
import ua.marchenko.artauction.infrastructure.auction.AuctionProtoFixture.randomCreateAuctionRequestProtoGrpc
import ua.marchenko.artauction.infrastructure.auction.AuctionProtoFixture.randomSuccessCreateAuctionResponseProto
import ua.marchenko.artauction.infrastructure.auction.AuctionProtoFixture.randomSuccessFindByIdResponseProto
import ua.marchenko.artauction.gateway.infrastructure.grpc.mapper.toAuctionProtoList
import ua.marchenko.artauction.gateway.infrastructure.grpc.mapper.toCreateAuctionRequestProtoInternal
import ua.marchenko.artauction.gateway.infrastructure.grpc.mapper.toCreateAuctionResponseProtoGrpc
import ua.marchenko.artauction.gateway.infrastructure.grpc.mapper.toFindAuctionByIdRequestProtoInternal
import ua.marchenko.artauction.gateway.infrastructure.grpc.mapper.toFindAuctionByIdResponseProtoGrpc
import ua.marchenko.artauction.getRandomString
import ua.marchenko.artauction.core.auction.exception.AuctionNotFoundException
import ua.marchenko.artauction.core.auction.exception.InvalidAuctionOperationException
import ua.marchenko.grpcapi.input.reqreply.auction.FindAuctionByIdRequest
import ua.marchenko.grpcapi.input.reqreply.auction.FindAuctionByIdResponse
import ua.marchenko.internal.input.reqreply.auction.CreateAuctionRequest
import ua.marchenko.internal.input.reqreply.auction.CreateAuctionResponse
import ua.marchenko.internal.input.reqreply.auction.FindAllAuctionsResponse

class AuctionProtoMapperTest {

    @Test
    fun `should build FindAuctionByIdRequestProtoInternal from FindAuctionByIdRequestProtoGrpc`() {
        // GIVEN
        val id = getRandomString()
        val request = FindAuctionByIdRequest.newBuilder().setId(id).build()
        val expectedResponse =
            ua.marchenko.internal.input.reqreply.auction.FindAuctionByIdRequest.newBuilder().setId(id).build()

        // WHEN
        val result = request.toFindAuctionByIdRequestProtoInternal()

        // THEN
        assertEquals(expectedResponse, result)
    }

    @Test
    fun `should build FindAuctionByIdResponseGrpc success when FindAuctionByIdResponseInternal is success`() {
        // GIVEN
        val internalResponse = randomSuccessFindByIdResponseProto()
        val expectedResponse = FindAuctionByIdResponse.newBuilder().apply {
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
        val internalResponse = ua.marchenko.internal.input.reqreply.auction.FindAuctionByIdResponse.newBuilder().build()

        // WHEN THEN
        val exception = assertThrows<IllegalStateException> { internalResponse.toFindAuctionByIdResponseProtoGrpc() }
        assertEquals(RESPONSE_NOT_SET_ERROR_MESSAGE, exception.message)
    }

    @ParameterizedTest
    @MethodSource("findAuctionByIdResponseProtoInternalFailureData")
    fun `should throw exception when the case in FindAuctionByIdResponseProtoInternal is failure`(
        response: ua.marchenko.internal.input.reqreply.auction.FindAuctionByIdResponse,
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
        val expectedRequest = CreateAuctionRequest.newBuilder().apply {
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
        val expectedResponse = ua.marchenko.grpcapi.input.reqreply.auction.CreateAuctionResponse.newBuilder().apply {
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
        response: CreateAuctionResponse,
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
        val internalResponse = CreateAuctionResponse.newBuilder().build()

        // WHEN THEN
        val exception = assertThrows<IllegalStateException> { internalResponse.toCreateAuctionResponseProtoGrpc() }
        assertEquals(RESPONSE_NOT_SET_ERROR_MESSAGE, exception.message)
    }

    @Test
    fun `should throw exception when the case in FindAllAuctionsResponseProtoInternal is not set`() {
        // GIVEN
        val internalResponse = FindAllAuctionsResponse.newBuilder().build()

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

