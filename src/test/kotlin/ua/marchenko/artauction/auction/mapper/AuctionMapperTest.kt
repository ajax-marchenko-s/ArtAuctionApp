package ua.marchenko.artauction.auction.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import ua.marchenko.artauction.artwork.mapper.toArtworkResponse
import ua.marchenko.artauction.auction.controller.dto.AuctionResponse
import ua.marchenko.artauction.auction.model.Auction
import ua.marchenko.artauction.common.artwork.getRandomArtwork
import ua.marchenko.artauction.common.auction.getRandomAuction
import ua.marchenko.artauction.common.auction.getRandomAuctionRequest
import ua.marchenko.artauction.user.mapper.toUserResponse
import kotlin.test.Test

class AuctionMapperTest {

    @Test
    fun `AuctionToAuctionResponse should return AuctionResponse if Auction has not null properties (except fields from business logic)`() {
        //GIVEN
        val auction = getRandomAuction()
        val expectedAuction = AuctionResponse(
            auction.id!!,
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
    fun `AuctionToAuctionResponse should throwIllegalArgumentException if Auction has null properties (except fields from bl)`() {
        //GIVEN
        val auction = getRandomAuction(artwork = null)

        //WHEN-THEN
        assertThrows<IllegalArgumentException> { auction.toAuctionResponse() }
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
