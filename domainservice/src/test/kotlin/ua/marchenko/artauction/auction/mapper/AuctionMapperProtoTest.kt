package ua.marchenko.artauction.auction.mapper

import artwork.random
import auction.AuctionProtoFixture
import auction.random
import com.google.protobuf.ByteString
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.random.Random
import kotlin.test.assertEquals
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import ua.marchenko.artauction.artwork.model.MongoArtwork
import ua.marchenko.artauction.auction.model.MongoAuction
import ua.marchenko.artauction.common.mongodb.id.toObjectId
import ua.marchenko.internal.commonmodels.general.BigDecimal as BigDecimalProto
import ua.marchenko.internal.commonmodels.general.BigDecimal.BigInteger as BigIntegerProto
import ua.marchenko.internal.commonmodels.auction.Auction as AuctionProto
import ua.marchenko.internal.commonmodels.auction.Auction.Bid as BidProto

class AuctionMapperProtoTest {

    @Test
    fun `should return AuctionProto when MongoAuction has all non-null properties`() {
        // GIVEN
        val mongoAuction = MongoAuction.random()
        val expectedAuctionProto = AuctionProto.newBuilder().also {
            it.id = mongoAuction.id!!.toHexString()
            it.artworkId = mongoAuction.artworkId!!.toHexString()
            it.startBid = mongoAuction.startBid!!.toBigDecimalProto()
            it.startedAt = mongoAuction.startedAt!!.toTimestampProto()
            it.finishedAt = mongoAuction.finishedAt!!.toTimestampProto()
            it.addAllBuyers(mongoAuction.buyers!!.map { it.toBidProto() })
        }.build()

        // WHEN
        val result = mongoAuction.toAuctionProto()

        // THEN
        assertEquals(expectedAuctionProto, result)
    }

    @ParameterizedTest
    @MethodSource("invalidMongoAuctionCasesWithExpectedErrorMessages")
    fun `should throw IllegalArgumentException with correct message when invalid MongoAuction to Proto mapping`(
        mongoAuction: MongoAuction, errorMessage: String,
    ) {
        // WHEN THEN
        val exception = assertThrows<IllegalArgumentException> {
            mongoAuction.toAuctionProto()
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
        val protoAuction = AuctionProtoFixture.randomAuctionProto()
        val expectedAuction = MongoAuction(
            id = protoAuction.id.toObjectId(),
            artworkId = protoAuction.artworkId.toObjectId(),
            startBid = protoAuction.startBid.toBigDecimal(),
            startedAt = protoAuction.startedAt.toLocalDateTime(),
            finishedAt = protoAuction.finishedAt.toLocalDateTime(),
            buyers = protoAuction.buyersList.map { it.toBid() }
        )

        // WHEN
        val result = protoAuction.toMongoAuction()

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
        @JvmStatic
        fun invalidMongoBidCasesWithExpectedErrorMessages(): List<Arguments> = listOf(
            Arguments.of(MongoAuction.Bid.random(buyerId = null), "Buyer id cannot be null"),
            Arguments.of(MongoAuction.Bid.random(bid = null), "Bid cannot be null"),
        )

        @JvmStatic
        fun invalidMongoAuctionCasesWithExpectedErrorMessages(): List<Arguments> = listOf(
            Arguments.of(MongoAuction.random(id = null), "Auction id cannot be null"),
            Arguments.of(MongoAuction.random(artwork = MongoArtwork.random(id = null)), "Artwork id cannot be null"),
        )
    }
}
