package ua.marchenko.artauction.domainservice.auction.infrastructure.rest.mapper

import kotlin.test.Test
import org.junit.jupiter.api.Assertions.assertEquals
import ua.marchenko.artauction.domainservice.auction.domain.Auction
import ua.marchenko.artauction.domainservice.auction.domain.projection.AuctionFull
import ua.marchenko.artauction.domainservice.auction.domain.random
import ua.marchenko.artauction.domainservice.auction.infrastructure.random
import ua.marchenko.artauction.domainservice.auction.infrastructure.rest.dto.AuctionFullResponse
import ua.marchenko.artauction.domainservice.auction.infrastructure.rest.dto.AuctionResponse
import ua.marchenko.artauction.domainservice.auction.infrastructure.rest.dto.CreateAuctionRequest
import ua.marchenko.artauction.domainservice.artwork.infrastructure.rest.mapper.toFullResponse
import ua.marchenko.artauction.domainservice.auction.domain.CreateAuction

class AuctionMapperTest {
    @Test
    fun `should return AuctionResponse from Auction domain`() {
        // GIVEN
        val auction = Auction.random()
        val expectedAuction = AuctionResponse(
            auction.id,
            auction.artworkId,
            auction.startBid,
            auction.buyers.map { it.toResponse() },
            auction.startedAt,
            auction.finishedAt,
        )

        // WHEN
        val result = auction.toResponse()

        // THEN
        assertEquals(expectedAuction, result)
    }

    @Test
    fun `should return Auction from CreateAuctionRequest`() {
        // GIVEN
        val auction = CreateAuctionRequest.random()
        val expectedCreateAuction = CreateAuction(
            auction.artworkId,
            auction.startBid,
            emptyList(),
            auction.startedAt,
            auction.finishedAt,
        )

        // WHEN
        val result = auction.toDomainCreate()

        // THEN
        assertEquals(expectedCreateAuction, result)
    }

    @Test
    fun `should return AuctionFullResponse from AuctionFull`() {
        // GIVEN
        val auctionFull = AuctionFull.random()
        val expectedResponse = AuctionFullResponse(
            auctionFull.id,
            auctionFull.artwork.toFullResponse(),
            auctionFull.startBid,
            auctionFull.buyers.map { it.toFullResponse() },
            auctionFull.startedAt,
            auctionFull.finishedAt,
        )

        // WHEN
        val result = auctionFull.toFullResponse()

        // THEN
        assertEquals(expectedResponse, result)
    }

    @Test
    fun `should return BidResponse from Bid`() {
        // GIVEN
        val bid = Auction.Bid.random()
        val expectedResponse = AuctionResponse.BidResponse(bid.buyerId, bid.bid)

        // WHEN
        val result = bid.toResponse()

        // THEN
        assertEquals(expectedResponse, result)
    }
}
