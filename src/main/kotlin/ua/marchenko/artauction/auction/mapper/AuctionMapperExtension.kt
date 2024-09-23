package ua.marchenko.artauction.auction.mapper

import java.time.LocalDateTime
import ua.marchenko.artauction.artwork.mapper.toArtworkFullResponse
import ua.marchenko.artauction.artwork.model.projection.ArtworkFull
import ua.marchenko.artauction.auction.controller.dto.AuctionFullResponse
import ua.marchenko.artauction.auction.controller.dto.CreateAuctionRequest
import ua.marchenko.artauction.auction.controller.dto.AuctionResponse
import ua.marchenko.artauction.auction.model.MongoAuction
import ua.marchenko.artauction.auction.model.projection.AuctionFull
import ua.marchenko.artauction.common.mongodb.id.toObjectId
import ua.marchenko.artauction.user.mapper.toUserResponse
import ua.marchenko.artauction.user.model.MongoUser

fun MongoAuction.toAuctionResponse() = AuctionResponse(
    requireNotNull(id) { "auction id cannot be null" }.toHexString(),
    artworkId?.toHexString() ?: "unknown",
    startBid ?: 0.0,
    buyers?.map { it.toBidResponse() }.orEmpty(),
    startedAt ?: LocalDateTime.MIN,
    finishedAt ?: LocalDateTime.MIN,
)

fun CreateAuctionRequest.toAuction() =
    MongoAuction(null, artworkId.toObjectId(), startBid, emptyList(), startedAt, finishedAt)

fun MongoAuction.Bid.toBidResponse() = AuctionResponse.BidResponse(
    buyerId?.toHexString().orEmpty(),
    bid ?: 0.0
)

fun AuctionFull.toAuctionFullResponse() = AuctionFullResponse(
    requireNotNull(id) { "artwork id cannot be null" }.toHexString(),
    artwork?.toArtworkFullResponse() ?: ArtworkFull().toArtworkFullResponse(),
    startBid ?: 0.0,
    buyers?.map { it.toBidFullResponse() }.orEmpty(),
    startedAt ?: LocalDateTime.MIN,
    finishedAt ?: LocalDateTime.MIN,
)

fun AuctionFull.BidFull.toBidFullResponse() = AuctionFullResponse.BidFullResponse(
    buyer?.toUserResponse() ?: MongoUser().toUserResponse(),
    bid ?: 0.0
)
