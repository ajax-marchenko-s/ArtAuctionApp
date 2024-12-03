package ua.marchenko.artauction.domainservice.auction.infrastructure.nats.mapper

import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import ua.marchenko.artauction.domainservice.auction.domain.Auction
import ua.marchenko.artauction.domainservice.auction.domain.random
import ua.marchenko.artauction.domainservice.auction.infrastructure.AuctionProtoFixture.randomCreateAuctionRequestProto
import ua.marchenko.artauction.core.auction.exception.AuctionNotFoundException
import ua.marchenko.artauction.core.auction.exception.InvalidAuctionOperationException
import ua.marchenko.artauction.domainservice.auction.domain.CreateAuction
import ua.marchenko.artauction.domainservice.auction.infrastructure.common.mapper.CommonMapper.toAuctionProto
import ua.marchenko.artauction.domainservice.auction.infrastructure.common.mapper.CommonMapper.toBigDecimal
import ua.marchenko.artauction.domainservice.auction.infrastructure.common.mapper.CommonMapper.toLocalDateTime
import ua.marchenko.internal.input.reqreply.auction.CreateAuctionResponse
import ua.marchenko.internal.input.reqreply.auction.FindAllAuctionsResponse
import ua.marchenko.internal.input.reqreply.auction.FindAuctionByIdResponse

class AuctionProtoMapperTest {

    @Test
    fun `should return CreateAuction from CreateAuctionRequestProto`() {
        // GIVEN
        val fixedClock = Clock.fixed(Instant.now(), ZoneId.systemDefault())
        val requestProto = randomCreateAuctionRequestProto()
        val expectedResponse = CreateAuction(
            artworkId = requestProto.artworkId,
            startBid = requestProto.startBid.toBigDecimal(),
            startedAt = requestProto.startedAt.toLocalDateTime(fixedClock),
            finishedAt = requestProto.finishedAt.toLocalDateTime(fixedClock),
            buyers = emptyList(),
        )

        // WHEN
        val result = requestProto.toDomainCreate(fixedClock)

        // THEN
        assertEquals(expectedResponse, result)
    }

    @Test
    fun `should build CreateAuctionResponseProto success from Auction`() {
        // GIVEN
        val fixedClock = Clock.fixed(Instant.now(), ZoneId.systemDefault())
        val mongoAuction = Auction.random()
        val expectedResponse = CreateAuctionResponse.newBuilder().apply {
            successBuilder.auction = mongoAuction.toAuctionProto(fixedClock)
        }.build()

        // WHEN
        val result = mongoAuction.toCreateAuctionSuccessResponseProto(fixedClock)

        // THEN
        assertEquals(expectedResponse, result)
    }

    @ParameterizedTest
    @MethodSource("throwableWithExpectedFailureCreateAuctionResponseProto")
    fun `should return correct CreateAuctionResponseProto failure when call on throwable `(
        throwable: Throwable, response: CreateAuctionResponse,
    ) {
        // WHEN THEN
        assertEquals(response, throwable.toCreateAuctionFailureResponseProto())
    }

    @Test
    fun `should return FindAllAuctionsResponseProto with failure when called on throwable `() {
        // GIVEN
        val error = Throwable(ERROR_MESSAGE)
        val expectedResponse = FindAllAuctionsResponse.newBuilder().apply {
            failureBuilder.message = ERROR_MESSAGE
        }.build()

        // WHEN
        val result = error.toFindAllAuctionsFailureResponseProto()

        // THEN
        assertEquals(expectedResponse, result)
    }

    @Test
    fun `should build FindAllAuctionsResponseProto success from List of Auction`() {
        // GIVEN
        val fixedClock = Clock.fixed(Instant.now(), ZoneId.systemDefault())
        val auctionList = List(3) { Auction.random() }
        val expectedResponse = FindAllAuctionsResponse.newBuilder().apply {
            successBuilder.addAllAuctions(auctionList.map { auction -> auction.toAuctionProto(fixedClock) })
        }.build()

        // WHEN
        val result = auctionList.toFindAllAuctionsSuccessResponseProto(fixedClock)

        // THEN
        assertEquals(expectedResponse, result)
    }

    @Test
    fun `should return FindAuctionByIdResponseProto with Success when called on Auction`() {
        // GIVEN
        val fixedClock = Clock.fixed(Instant.now(), ZoneId.systemDefault())
        val auction = Auction.random()
        val expectedResponse = FindAuctionByIdResponse.newBuilder().apply {
            successBuilder.auction = auction.toAuctionProto(fixedClock)
        }.build()

        // WHEN
        val result = auction.toFindAuctionByIdSuccessResponseProto(fixedClock)

        // THEN
        assertEquals(expectedResponse, result)
    }

    @ParameterizedTest
    @MethodSource("throwableWithExpectedFailureFindAuctionByIdResponseProto")
    fun `should return correct FindAuctionByIdResponseProto failure when call on throwable `(
        throwable: Throwable, response: FindAuctionByIdResponse,
    ) {
        // WHEN THEN
        assertEquals(response, throwable.toFindAuctionByIdFailureResponseProto())
    }

    companion object {
        private const val ERROR_MESSAGE = "error message"

        @JvmStatic
        fun throwableWithExpectedFailureCreateAuctionResponseProto(): List<Arguments> = listOf(
            Arguments.of(
                InvalidAuctionOperationException(ERROR_MESSAGE),
                CreateAuctionResponse.newBuilder().apply {
                    failureBuilder.invalidAuctionOperationBuilder
                    failureBuilder.message = ERROR_MESSAGE
                }.build()
            ),
            Arguments.of(Exception(ERROR_MESSAGE), CreateAuctionResponse.newBuilder().apply {
                failureBuilder.message = ERROR_MESSAGE
            }.build()),
        )

        @JvmStatic
        fun throwableWithExpectedFailureFindAuctionByIdResponseProto(): List<Arguments> = listOf(
            Arguments.of(
                AuctionNotFoundException("id"),
                FindAuctionByIdResponse.newBuilder().apply {
                    failureBuilder.notFoundByIdBuilder
                    failureBuilder.message = "Auction with ID id not found"
                }.build()
            ),
            Arguments.of(Exception(ERROR_MESSAGE), FindAuctionByIdResponse.newBuilder().apply {
                failureBuilder.message = ERROR_MESSAGE
            }.build()),
        )
    }
}
