package auction.mapper

import auction.AuctionProtoFixture
import auction.AuctionProtoFixture.randomCreateAuctionRequestProtoGrpc
import auction.AuctionProtoFixture.randomSuccessCreateAuctionResponseProto
import auction.AuctionProtoFixture.randomSuccessFindByIdResponseProto
import getRandomString
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import ua.marchenko.core.auction.exception.AuctionNotFoundException
import ua.marchenko.core.auction.exception.InvalidAuctionOperationException
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

    companion object {
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
