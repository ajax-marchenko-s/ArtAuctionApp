package ua.marchenko.artauction.domainservice.auction.infrastructure.common.mapper

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
import ua.marchenko.artauction.domainservice.auction.domain.Auction
import ua.marchenko.artauction.domainservice.auction.domain.Auction.Bid
import ua.marchenko.artauction.domainservice.auction.domain.random
import ua.marchenko.artauction.domainservice.auction.infrastructure.AuctionProtoFixture
import ua.marchenko.commonmodels.auction.Auction as AuctionProto
import ua.marchenko.commonmodels.auction.Auction.Bid as BidProto

class AuctionProtoMapperTest {

    @Test
    fun `should return AuctionProto from Auction`() {
        // GIVEN
        val fixedClock = Clock.fixed(Instant.now(), ZoneId.systemDefault())
        val auction = Auction.random()
        val expectedAuctionProto = AuctionProto.newBuilder().also {
            it.id = auction.id!!
            it.artworkId = auction.artworkId
            it.startBid = auction.startBid.toBigDecimalProto()
            it.startedAt = auction.startedAt.toTimestampProto(fixedClock)
            it.finishedAt = auction.finishedAt.toTimestampProto(fixedClock)
            it.addAllBuyers(auction.buyers.map { it.toBidProto() })
        }.build()

        // WHEN
        val result = auction.toAuctionProto(fixedClock)

        // THEN
        assertEquals(expectedAuctionProto, result)
    }

    @Test
    fun `should throw IllegalArgumentException when Auction with null id to Proto mapping`() {
        // GIVEN
        val fixedClock = Clock.fixed(Instant.now(), ZoneId.systemDefault())
        val auction = Auction.random(id = null)

        // WHEN THEN
        val exception = assertThrows<IllegalArgumentException> {
            auction.toAuctionProto(fixedClock)
        }
        Assertions.assertEquals("Auction id cannot be null", exception.message)
    }

    @Test
    fun `should return BidProto when Bid has all non-null properties`() {
        // GIVEN
        val mongoBid = Bid.random()
        val expectedBidProto = BidProto.newBuilder().also {
            it.bid = mongoBid.bid.toBigDecimalProto()
            it.buyerId = mongoBid.buyerId
        }.build()

        // WHEN
        val result = mongoBid.toBidProto()

        // THEN
        assertEquals(expectedBidProto, result)
    }

    @Test
    fun `should return BigDecimalProto from BigDecimal`() {
        // GIVEN
        val bigDecimal = BigDecimal.valueOf(Random.nextDouble(1.0, 100.0))
        val expectedBigDecimal = ua.marchenko.commonmodels.general.BigDecimal.newBuilder().also {
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
        val expectedBigInteger = ua.marchenko.commonmodels.general.BigDecimal.BigInteger.newBuilder().also {
            it.value = ByteString.copyFrom(bigInteger.toByteArray())
        }.build()

        // WHEN
        val result = bigInteger.toBigIntegerProto()

        // THEN
        assertEquals(expectedBigInteger, result)
    }

    @Test
    fun `should return Auction from AuctionProto`() {
        // GIVEN
        val fixedClock = Clock.fixed(Instant.now(), ZoneId.systemDefault())
        val protoAuction = AuctionProtoFixture.randomAuctionProto()
        val expectedAuction = Auction(
            id = protoAuction.id,
            artworkId = protoAuction.artworkId,
            startBid = protoAuction.startBid.toBigDecimal(),
            startedAt = protoAuction.startedAt.toLocalDateTime(fixedClock),
            finishedAt = protoAuction.finishedAt.toLocalDateTime(fixedClock),
            buyers = protoAuction.buyersList.map { it.toDomain() }
        )

        // WHEN
        val result = protoAuction.toDomain(fixedClock)

        // THEN
        assertEquals(expectedAuction, result)
    }

    @Test
    fun `should return Bid from BidProto`() {
        // GIVEN
        val protoBid = AuctionProtoFixture.randomBidProto()
        val expectedBid = Bid(
            buyerId = protoBid.buyerId,
            bid = protoBid.bid.toBigDecimal()
        )

        // WHEN
        val result = protoBid.toDomain()

        // THEN
        assertEquals(expectedBid, result)
    }
}
