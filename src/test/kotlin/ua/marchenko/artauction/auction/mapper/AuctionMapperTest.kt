package ua.marchenko.artauction.auction.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import ua.marchenko.artauction.auction.controller.dto.AuctionResponse
import auction.random
import java.math.BigDecimal
import kotlin.test.Test
import org.junit.jupiter.api.assertThrows
import ua.marchenko.artauction.artwork.mapper.toFullResponse
import ua.marchenko.artauction.auction.controller.dto.AuctionFullResponse
import ua.marchenko.artauction.auction.controller.dto.CreateAuctionRequest
import ua.marchenko.artauction.auction.model.MongoAuction
import ua.marchenko.artauction.auction.model.projection.AuctionFull
import ua.marchenko.artauction.common.mongodb.id.toObjectId

class AuctionMapperTest {

    @Test
    fun `should return AuctionResponse when Auction has not null properties (except fields from business logic)`() {
        //GIVEN
        val auction = MongoAuction.random()
        val expectedAuction = AuctionResponse(
            auction.id!!.toHexString(),
            auction.artworkId!!.toHexString(),
            auction.startBid!!,
            auction.buyers!!.map { it.toResponse() },
            auction.startedAt!!,
            auction.finishedAt!!
        )

        //WHEN
        val result = auction.toResponse()

        //THEN
        assertEquals(expectedAuction, result)
    }

    @Test
    fun `should set default values when Auction has null properties (except fields from bl)`() {
        // GIVEN
        val auction = MongoAuction.random(startBid = null)
        val expectedAuction = AuctionResponse(
            auction.id!!.toHexString(),
            auction.artworkId!!.toHexString(),
            BigDecimal(0.0),
            auction.buyers!!.map { it.toResponse() },
            auction.startedAt!!,
            auction.finishedAt!!
        )

        //WHEN
        val result = auction.toResponse()

        //THEN
        assertEquals(expectedAuction, result)
    }

    @Test
    fun `should throw exception when Auction id is null`() {
        // GIVEN
        val auction = MongoAuction.random(id = null)

        // WHEN THEN
        val exception = assertThrows<IllegalArgumentException> {
            auction.toResponse()
        }
        assertEquals("auction id cannot be null", exception.message)
    }

    @Test
    fun `should return Auction`() {
        //GIVEN
        val auction = CreateAuctionRequest.random()
        val expectedAuction = MongoAuction(
            null,
            auction.artworkId.toObjectId(),
            auction.startBid,
            emptyList(),
            auction.startedAt,
            auction.finishedAt
        )

        //WHEN
        val result = auction.toMongo()

        //THEN
        assertEquals(expectedAuction, result)
    }

    @Test
    fun `should return AuctionFullResponse when AuctionFull has all non-null properties`() {
        // GIVEN
        val auctionFull = AuctionFull.random()
        val expectedResponse = AuctionFullResponse(
            auctionFull.id!!.toHexString(),
            auctionFull.artwork!!.toFullResponse(),
            auctionFull.startBid!!,
            auctionFull.buyers!!.map { it.toFullResponse() },
            auctionFull.startedAt!!,
            auctionFull.finishedAt!!
        )

        // WHEN
        val result = auctionFull.toFullResponse()

        // THEN
        assertEquals(expectedResponse, result)
    }

    @Test
    fun `should return AuctionFullResponse with default values when AuctionFull has null properties`() {
        // GIVEN
        val auctionFull = AuctionFull.random(buyers = null)
        val expectedResponse = AuctionFullResponse(
            auctionFull.id!!.toHexString(),
            auctionFull.artwork!!.toFullResponse(),
            auctionFull.startBid!!,
            emptyList(),
            auctionFull.startedAt!!,
            auctionFull.finishedAt!!
        )

        //WHEN
        val result = auctionFull.toFullResponse()

        //THEN
        assertEquals(expectedResponse, result)
    }

    @Test
    fun `should return BidResponse when Bid has all non-null properties`() {
        // GIVEN
        val bid = MongoAuction.Bid.random()
        val expectedResponse = AuctionResponse.BidResponse(bid.buyerId!!.toHexString(), bid.bid!!)

        // WHEN
        val result = bid.toResponse()

        // THEN
        assertEquals(expectedResponse, result)
    }

    @Test
    fun `should return BidResponse with default values when Bid has all null properties`() {
        // GIVEN
        val bid = MongoAuction.Bid.random(buyerId = null, bid = null)
        val expectedResponse = AuctionResponse.BidResponse("", BigDecimal(0.0))

        // WHEN
        val result = bid.toResponse()

        // THEN
        assertEquals(expectedResponse, result)
    }
}
