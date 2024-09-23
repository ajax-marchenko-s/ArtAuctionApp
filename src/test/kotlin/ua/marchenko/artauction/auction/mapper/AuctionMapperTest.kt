package ua.marchenko.artauction.auction.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import ua.marchenko.artauction.auction.controller.dto.AuctionResponse
import auction.random
import kotlin.test.Test
import org.junit.jupiter.api.assertThrows
import ua.marchenko.artauction.auction.controller.dto.CreateAuctionRequest
import ua.marchenko.artauction.auction.model.MongoAuction
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
            auction.buyers!!.map { it.toBidResponse() },
            auction.startedAt!!,
            auction.finishedAt!!
        )

        //WHEN
        val result = auction.toAuctionResponse()

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
            BID,
            auction.buyers!!.map { it.toBidResponse() },
            auction.startedAt!!,
            auction.finishedAt!!
        )

        //WHEN
        val result = auction.toAuctionResponse()

        //THEN
        assertEquals(expectedAuction, result)
    }

    @Test
    fun `should throw exception when Auction id is null`() {
        // GIVEN
        val auction = MongoAuction.random(id = null)

        // WHEN THEN
        val exception = assertThrows<IllegalArgumentException> {
            auction.toAuctionResponse()
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
        val result = auction.toAuction()

        //THEN
        assertEquals(expectedAuction, result)
    }

    companion object {
        const val BID = 0.0
    }
}
