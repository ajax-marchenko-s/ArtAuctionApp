package ua.marchenko.artauction.auction.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import ua.marchenko.artauction.artwork.mapper.toArtworkResponse
import ua.marchenko.artauction.auction.controller.dto.AuctionResponse
import ua.marchenko.artauction.auction.model.Auction
import artwork.getRandomArtwork
import auction.getRandomAuction
import auction.getRandomAuctionRequest
import ua.marchenko.artauction.user.mapper.toUserResponse
import kotlin.test.Test

class AuctionMapperTest {

    companion object {
        const val BID = 0.0
    }

    @Test
    fun `AuctionToAuctionResponse should return AuctionResponse if Auction has not null properties (except fields from business logic)`() {
        //GIVEN
        val auction = getRandomAuction()
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
    fun `AuctionToAuctionResponse should set default values if Auction has null properties (except fields from bl)`() {
        // GIVEN
        val auction = getRandomAuction(bid = null)
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
    fun `AuctionRequestToAuction should return Auction`() {
        //GIVEN
        val auction = getRandomAuctionRequest()
        val artwork = getRandomArtwork()
        val expectedAuction = Auction(null, artwork, auction.bid, null, auction.startedAt, auction.finishedAt)

        //WHEN
        val result = auction.toAuction(artwork, null)

        //THEN
        assertEquals(expectedAuction, result)
    }
}
