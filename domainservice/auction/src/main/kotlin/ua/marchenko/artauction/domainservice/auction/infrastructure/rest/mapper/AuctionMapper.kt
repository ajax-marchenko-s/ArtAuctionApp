package ua.marchenko.artauction.domainservice.auction.infrastructure.rest.mapper

import ua.marchenko.artauction.domainservice.auction.domain.Auction
import ua.marchenko.artauction.domainservice.auction.domain.projection.AuctionFull
import ua.marchenko.artauction.domainservice.auction.infrastructure.rest.dto.AuctionFullResponse
import ua.marchenko.artauction.domainservice.auction.infrastructure.rest.dto.AuctionResponse
import ua.marchenko.artauction.domainservice.auction.infrastructure.rest.dto.CreateAuctionRequest
import ua.marchenko.artauction.domainservice.user.infrastructure.rest.mapper.toResponse
import ua.marchenko.artauction.domainservice.artwork.infrastructure.rest.mapper.toFullResponse
import ua.marchenko.artauction.domainservice.auction.domain.CreateAuction

fun Auction.toResponse() = AuctionResponse(
    id = id,
    artworkId = artworkId,
    startBid = startBid,
    buyers = buyers.map { it.toResponse() },
    startedAt = startedAt,
    finishedAt = finishedAt,
)

fun CreateAuctionRequest.toDomainCreate() =
    CreateAuction(
        artworkId = artworkId,
        startBid = startBid,
        startedAt = startedAt,
        finishedAt = finishedAt,
    )

fun Auction.Bid.toResponse() = AuctionResponse.BidResponse(buyerId, bid)

fun AuctionFull.toFullResponse() = AuctionFullResponse(
    id = id,
    artwork = artwork.toFullResponse(),
    startBid = startBid,
    buyers = buyers.map { it.toFullResponse() },
    startedAt = startedAt,
    finishedAt = finishedAt,
)

fun AuctionFull.BidFull.toFullResponse() = AuctionFullResponse.BidFullResponse(buyer.toResponse(), bid)
