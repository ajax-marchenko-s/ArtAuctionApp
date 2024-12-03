package ua.marchenko.artauction.domainservice.auction.infrastructure.mongo.mapper

import java.math.BigDecimal
import java.time.LocalDateTime
import ua.marchenko.artauction.domainservice.auction.domain.Auction
import ua.marchenko.artauction.domainservice.auction.domain.projection.AuctionFull
import ua.marchenko.artauction.domainservice.auction.infrastructure.mongo.entity.MongoAuction
import ua.marchenko.artauction.domainservice.auction.infrastructure.mongo.entity.projection.MongoAuctionFull
import ua.marchenko.artauction.domainservice.user.infrastructure.mongo.entity.MongoUser
import ua.marchenko.artauction.domainservice.user.infrastructure.mongo.mapper.toDomain
import ua.marchenko.artauction.domainservice.artwork.infrastructure.mongo.entity.projection.MongoArtworkFull
import ua.marchenko.artauction.domainservice.artwork.infrastructure.mongo.mapper.toDomain
import ua.marchenko.artauction.domainservice.auction.domain.CreateAuction
import ua.marchenko.artauction.domainservice.common.infrastructure.mongodb.id.toObjectId

fun MongoAuction.toDomain() = Auction(
    requireNotNull(id) { "auction id cannot be null" }.toHexString(),
    artworkId?.toHexString() ?: "unknown",
    startBid ?: BigDecimal(0.0),
    buyers?.map { it.toDomain() }.orEmpty(),
    startedAt ?: LocalDateTime.MIN,
    finishedAt ?: LocalDateTime.MIN,
)

fun CreateAuction.toMongo() =
    MongoAuction(null, artworkId.toObjectId(), startBid, buyers.map { it.toMongo() }, startedAt, finishedAt)

fun Auction.Bid.toMongo() = MongoAuction.Bid(buyerId.toObjectId(), bid)

fun MongoAuction.Bid.toDomain() = Auction.Bid(
    buyerId?.toHexString().orEmpty(),
    bid ?: BigDecimal(0.0)
)

fun MongoAuctionFull.toDomain() = AuctionFull(
    requireNotNull(id) { "artwork id cannot be null" }.toHexString(),
    artwork?.toDomain() ?: MongoArtworkFull().toDomain(),
    startBid ?: BigDecimal(0.0),
    buyers?.map { it.toDomain() }.orEmpty(),
    startedAt ?: LocalDateTime.MIN,
    finishedAt ?: LocalDateTime.MIN,
)

fun MongoAuctionFull.BidFull.toDomain() = AuctionFull.BidFull(
    buyer?.toDomain() ?: MongoUser().toDomain(),
    bid ?: BigDecimal(0.0)
)
