package ua.marchenko.artauction.auction.mapper

import auction.AuctionProtoFixture
import auction.AuctionProtoFixture.randomCreateAuctionRequestProto
import auction.random
import com.google.protobuf.ByteString
import java.math.BigDecimal
import java.math.BigInteger
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import kotlin.random.Random
import kotlin.test.assertEquals
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import ua.marchenko.artauction.auction.controller.dto.CreateAuctionRequest
import ua.marchenko.artauction.auction.model.MongoAuction
import ua.marchenko.artauction.common.mongodb.id.toObjectId
import ua.marchenko.core.auction.exception.AuctionNotFoundException
import ua.marchenko.core.auction.exception.InvalidAuctionOperationException
import ua.marchenko.commonmodels.general.BigDecimal as BigDecimalProto
import ua.marchenko.commonmodels.general.BigDecimal.BigInteger as BigIntegerProto
import ua.marchenko.commonmodels.auction.Auction as AuctionProto
import ua.marchenko.commonmodels.auction.Auction.Bid as BidProto
import ua.marchenko.internal.input.reqreply.auction.FindAllAuctionsResponse as FindAllAuctionsResponseProto
import ua.marchenko.internal.input.reqreply.auction.CreateAuctionResponse as CreateAuctionResponseProto
import ua.marchenko.internal.input.reqreply.auction.FindAuctionByIdResponse as FindAuctionByIdResponseProto

class AuctionMapperProtoTest {

    @Test
    fun `should return CreateAuctionRequest from CreateAuctionRequestProto`() {
        // GIVEN
        val fixedClock = Clock.fixed(Instant.now(), ZoneId.systemDefault())
        val requestProto = randomCreateAuctionRequestProto()
        val expectedResponse = CreateAuctionRequest(
            requestProto.artworkId,
            requestProto.startBid.toBigDecimal(),
            requestProto.startedAt.toLocalDateTime(fixedClock),
            requestProto.finishedAt.toLocalDateTime(fixedClock),
        )

        // WHEN
        val result = requestProto.toCreateAuctionRequest(fixedClock)

        // THEN
        assertEquals(expectedResponse, result)
    }

    @Test
    fun `should build CreateAuctionResponseProto success from MongoAuction`() {
        // GIVEN
        val fixedClock = Clock.fixed(Instant.now(), ZoneId.systemDefault())
        val mongoAuction = MongoAuction.random()
        val expectedResponse = CreateAuctionResponseProto.newBuilder().apply {
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
        throwable: Throwable, response: CreateAuctionResponseProto,
    ) {
        // WHEN THEN
        assertEquals(response, throwable.toCreateAuctionFailureResponseProto())
    }

    @Test
    fun `should return FindAllAuctionsResponseProto with failure when called on throwable `() {
        // GIVEN
        val error = Throwable(ERROR_MESSAGE)
        val expectedResponse = FindAllAuctionsResponseProto.newBuilder().apply {
            failureBuilder.message = ERROR_MESSAGE
        }.build()

        // WHEN
        val result = error.toFindAllAuctionsFailureResponseProto()

        // THEN
        assertEquals(expectedResponse, result)
    }

    @Test
    fun `should build FindAllAuctionsResponseProto success from List of MongoAuction`() {
        // GIVEN
        val fixedClock = Clock.fixed(Instant.now(), ZoneId.systemDefault())
        val auctionList = List(3) { MongoAuction.random() }
        val expectedResponse = FindAllAuctionsResponseProto.newBuilder().apply {
            successBuilder.addAllAuctions(auctionList.map { auction -> auction.toAuctionProto(fixedClock) })
        }.build()

        // WHEN
        val result = auctionList.toFindAllAuctionsSuccessResponseProto(fixedClock)

        // THEN
        assertEquals(expectedResponse, result)
    }

    @Test
    fun `should return FindAuctionByIdResponseProto with Success when called on MongoAuction`() {
        // GIVEN
        val fixedClock = Clock.fixed(Instant.now(), ZoneId.systemDefault())
        val mongoAuction = MongoAuction.random()
        val expectedResponse = FindAuctionByIdResponseProto.newBuilder().apply {
            successBuilder.auction = mongoAuction.toAuctionProto(fixedClock)
        }.build()

        // WHEN
        val result = mongoAuction.toFindAuctionByIdSuccessResponseProto(fixedClock)

        // THEN
        assertEquals(expectedResponse, result)
    }

    @ParameterizedTest
    @MethodSource("throwableWithExpectedFailureFindAuctionByIdResponseProto")
    fun `should return correct FindAuctionByIdResponseProto failure when call on throwable `(
        throwable: Throwable, response: FindAuctionByIdResponseProto,
    ) {
        // WHEN THEN
        assertEquals(response, throwable.toFindAuctionByIdFailureResponseProto())
    }

    @Test
    fun `should return AuctionProto when MongoAuction has all non-null properties`() {
        // GIVEN
        val fixedClock = Clock.fixed(Instant.now(), ZoneId.systemDefault())
        val mongoAuction = MongoAuction.random()
        val expectedAuctionProto = AuctionProto.newBuilder().also {
            it.id = mongoAuction.id!!.toHexString()
            it.artworkId = mongoAuction.artworkId!!.toHexString()
            it.startBid = mongoAuction.startBid!!.toBigDecimalProto()
            it.startedAt = mongoAuction.startedAt!!.toTimestampProto(fixedClock)
            it.finishedAt = mongoAuction.finishedAt!!.toTimestampProto(fixedClock)
            it.addAllBuyers(mongoAuction.buyers!!.map { it.toBidProto() })
        }.build()

        // WHEN
        val result = mongoAuction.toAuctionProto(fixedClock)

        // THEN
        assertEquals(expectedAuctionProto, result)
    }

    @ParameterizedTest
    @MethodSource("invalidMongoAuctionCasesWithExpectedErrorMessages")
    fun `should throw IllegalArgumentException with correct message when invalid MongoAuction to Proto mapping`(
        mongoAuction: MongoAuction, errorMessage: String,
    ) {
        // GIVEN
        val fixedClock = Clock.fixed(Instant.now(), ZoneId.systemDefault())

        // WHEN THEN
        val exception = assertThrows<IllegalArgumentException> {
            mongoAuction.toAuctionProto(fixedClock)
        }
        Assertions.assertEquals(errorMessage, exception.message)
    }

    @Test
    fun `should return BidProto when Bid has all non-null properties`() {
        // GIVEN
        val mongoBid = MongoAuction.Bid.random()
        val expectedBidProto = BidProto.newBuilder().also {
            it.bid = mongoBid.bid?.toBigDecimalProto()
            it.buyerId = mongoBid.buyerId!!.toHexString()
        }.build()

        // WHEN
        val result = mongoBid.toBidProto()

        // THEN
        assertEquals(expectedBidProto, result)
    }

    @ParameterizedTest
    @MethodSource("invalidMongoBidCasesWithExpectedErrorMessages")
    fun `should throw IllegalArgumentException with correct message when invalid Bid to Proto mapping`(
        mongoBid: MongoAuction.Bid, errorMessage: String,
    ) {
        // WHEN THEN
        val exception = assertThrows<IllegalArgumentException> {
            mongoBid.toBidProto()
        }
        Assertions.assertEquals(errorMessage, exception.message)
    }

    @Test
    fun `should return BigDecimalProto from BigDecimal`() {
        // GIVEN
        val bigDecimal = BigDecimal.valueOf(Random.nextDouble(1.0, 100.0))
        val expectedBigDecimal = BigDecimalProto.newBuilder().also {
            it.scale = bigDecimal.scale()
            it.intVal = bigDecimal.unscaledValue().toBigIntegerProto()
        }.build()

        // WHEN
        val result = bigDecimal.toBigDecimalProto()

        // THEN
        assertEquals(expectedBigDecimal, result)
    }

    @Test
    fun `should return BigIntegerProto from BigInteger`() {
        // GIVEN
        val bigInteger = BigInteger.valueOf(Random.nextLong(1L, 100L))
        val expectedBigInteger = BigIntegerProto.newBuilder().also {
            it.value = ByteString.copyFrom(bigInteger.toByteArray())
        }.build()

        // WHEN
        val result = bigInteger.toBigIntegerProto()

        // THEN
        assertEquals(expectedBigInteger, result)
    }

    @Test
    fun `should return MongoAuction from AuctionProto`() {
        // GIVEN
        val fixedClock = Clock.fixed(Instant.now(), ZoneId.systemDefault())
        val protoAuction = AuctionProtoFixture.randomAuctionProto()
        val expectedAuction = MongoAuction(
            id = protoAuction.id.toObjectId(),
            artworkId = protoAuction.artworkId.toObjectId(),
            startBid = protoAuction.startBid.toBigDecimal(),
            startedAt = protoAuction.startedAt.toLocalDateTime(fixedClock),
            finishedAt = protoAuction.finishedAt.toLocalDateTime(fixedClock),
            buyers = protoAuction.buyersList.map { it.toBid() }
        )

        // WHEN
        val result = protoAuction.toMongoAuction(fixedClock)

        // THEN
        assertEquals(expectedAuction, result)
    }

    @Test
    fun `should return Bid from BidProto`() {
        // GIVEN
        val protoBid = AuctionProtoFixture.randomBidProto()
        val expectedBid = MongoAuction.Bid(
            buyerId = protoBid.buyerId.toObjectId(),
            bid = protoBid.bid.toBigDecimal()
        )

        // WHEN
        val result = protoBid.toBid()

        // THEN
        assertEquals(expectedBid, result)
    }

    companion object {
        private const val ERROR_MESSAGE = "error message"

        @JvmStatic
        fun invalidMongoBidCasesWithExpectedErrorMessages(): List<Arguments> = listOf(
            Arguments.of(MongoAuction.Bid.random(buyerId = null), "Buyer id cannot be null"),
            Arguments.of(MongoAuction.Bid.random(bid = null), "Bid cannot be null"),
        )

        @JvmStatic
        fun invalidMongoAuctionCasesWithExpectedErrorMessages(): List<Arguments> = listOf(
            Arguments.of(MongoAuction.random(id = null), "Auction id cannot be null"),
            Arguments.of(MongoAuction.random(artworkId = null), "Artwork id cannot be null"),
        )

        @JvmStatic
        fun throwableWithExpectedFailureCreateAuctionResponseProto(): List<Arguments> = listOf(
            Arguments.of(
                InvalidAuctionOperationException(ERROR_MESSAGE),
                CreateAuctionResponseProto.newBuilder().also {
                    it.failureBuilder.invalidAuctionOperationBuilder
                    it.failureBuilder.message = ERROR_MESSAGE
                }.build()
            ),
            Arguments.of(Exception(ERROR_MESSAGE), CreateAuctionResponseProto.newBuilder().also {
                it.failureBuilder.message = ERROR_MESSAGE
            }.build()),
        )

        @JvmStatic
        fun throwableWithExpectedFailureFindAuctionByIdResponseProto(): List<Arguments> = listOf(
            Arguments.of(
                AuctionNotFoundException("id"),
                FindAuctionByIdResponseProto.newBuilder().apply {
                    failureBuilder.notFoundByIdBuilder
                    failureBuilder.message = "Auction with ID id not found"
                }.build()
            ),
            Arguments.of(Exception(ERROR_MESSAGE), FindAuctionByIdResponseProto.newBuilder().apply {
                failureBuilder.message = ERROR_MESSAGE
            }.build()),
        )
    }
}
