package ua.marchenko.artauction.auction.mapper

import java.math.BigDecimal
import java.time.LocalDateTime
import ua.marchenko.artauction.artwork.mapper.toFullResponse
import ua.marchenko.artauction.artwork.model.projection.ArtworkFull
import ua.marchenko.artauction.auction.controller.dto.AuctionFullResponse
import ua.marchenko.artauction.auction.controller.dto.CreateAuctionRequest
import ua.marchenko.artauction.auction.controller.dto.AuctionResponse
import ua.marchenko.artauction.auction.model.MongoAuction
import ua.marchenko.artauction.auction.model.projection.AuctionFull
import ua.marchenko.artauction.common.mongodb.id.toObjectId
import ua.marchenko.artauction.user.mapper.toResponse
import ua.marchenko.artauction.user.model.MongoUser

fun MongoAuction.toResponse() = AuctionResponse(
    requireNotNull(id) { "auction id cannot be null" }.toHexString(),
    artworkId?.toHexString() ?: "unknown",
    startBid ?: BigDecimal(0.0),
    buyers?.map { it.toResponse() }.orEmpty(),
    startedAt ?: LocalDateTime.MIN,
    finishedAt ?: LocalDateTime.MIN,
)

fun CreateAuctionRequest.toMongo() =
    MongoAuction(null, artworkId.toObjectId(), startBid, emptyList(), startedAt, finishedAt)

fun MongoAuction.Bid.toResponse() = AuctionResponse.BidResponse(
    buyerId?.toHexString().orEmpty(),
    bid ?: BigDecimal(0.0)
)

fun AuctionFull.toFullResponse() = AuctionFullResponse(
    requireNotNull(id) { "artwork id cannot be null" }.toHexString(),
    artwork?.toFullResponse() ?: ArtworkFull().toFullResponse(),
    startBid ?: BigDecimal(0.0),
    buyers?.map { it.toFullResponse() }.orEmpty(),
    startedAt ?: LocalDateTime.MIN,
    finishedAt ?: LocalDateTime.MIN,
)

fun AuctionFull.BidFull.toFullResponse() = AuctionFullResponse.BidFullResponse(
    buyer?.toResponse() ?: MongoUser().toResponse(),
    bid ?: BigDecimal(0.0)
)
