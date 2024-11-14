package auction.mapper

import auction.AuctionProtoFixture
import auction.AuctionProtoFixture.randomCreateAuctionRequestProtoGrpc
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
import ua.marchenko.gateway.auction.mapper.toFindAuctionByIdResponseProtoGrpc
import ua.marchenko.internal.input.reqreply.auction.FindAuctionByIdResponse as FindAuctionByIdResponseProtoInternal
import ua.marchenko.internal.input.reqreply.auction.CreateAuctionResponse as CreateAuctionResponseProtoInternal
import ua.marchenko.internal.input.reqreply.auction.CreateAuctionRequest as CreateArtworkRequestProtoInternal

class AuctionProtoMapperTest {

    @Test
    fun `should build CreateAuctionRequestProtoInternal from CreateAuctionRequestProtoGrpc`() {
        // GIVEN
        val request = randomCreateAuctionRequestProtoGrpc()
        val expectedRequestProto = CreateArtworkRequestProtoInternal.newBuilder().also {
            it.artworkId = request.artworkId
            it.startBid = request.startBid
            it.startedAt = request.startedAt
            it.finishedAt = request.finishedAt
        }.build()

        // WHEN
        val result = request.toCreateAuctionRequestProtoInternal()

        // THEN
        assertEquals(expectedRequestProto, result)
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
