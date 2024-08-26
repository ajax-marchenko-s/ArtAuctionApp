package ua.marchenko.artauction.auction.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import ua.marchenko.artauction.artwork.mapper.toArtworkResponse
import ua.marchenko.artauction.auction.controller.dto.AuctionResponse
import ua.marchenko.artauction.auction.model.Auction
import artwork.random
import auction.random
import ua.marchenko.artauction.user.mapper.toUserResponse
import kotlin.test.Test
import ua.marchenko.artauction.artwork.model.Artwork
import ua.marchenko.artauction.auction.controller.dto.CreateAuctionRequest

class AuctionMapperTest {

    @Test
    fun `should return AuctionResponse when Auction has not null properties (except fields from business logic)`() {
        //GIVEN
        val auction = Auction.random()
        val expectedAuction = AuctionResponse(
            auction.id!!.toString(),
            auction.artwork!!.toArtworkResponse(),
            auction.bid!!,
            auction.buyer?.toUserResponse(),
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
        val auction = Auction.random(bid = null)
        val expectedAuction = AuctionResponse(
            auction.id!!.toString(),
            auction.artwork!!.toArtworkResponse(),
            BID,
            auction.buyer?.toUserResponse(),
            auction.startedAt!!,
            auction.finishedAt!!
        )

        //WHEN
        val result = auction.toAuctionResponse()

        //THEN
        assertEquals(expectedAuction, result)
    }

    @Test
    fun `should return Auction`() {
        //GIVEN
        val auction = CreateAuctionRequest.random()
        val artwork = Artwork.random()
        val expectedAuction = Auction(null, artwork, auction.bid, null, auction.startedAt, auction.finishedAt)

        //WHEN
        val result = auction.toAuction(artwork, null)

        //THEN
        assertEquals(expectedAuction, result)
    }

    companion object {
        const val BID = 0.0
    }
}
