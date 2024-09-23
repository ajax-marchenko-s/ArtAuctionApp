package auction

import ua.marchenko.artauction.auction.controller.dto.CreateAuctionRequest
import ua.marchenko.artauction.auction.model.MongoAuction
import artwork.random
import getRandomObjectId
import java.time.LocalDateTime
import ua.marchenko.artauction.artwork.model.MongoArtwork
import ua.marchenko.artauction.artwork.model.projection.ArtworkFull
import ua.marchenko.artauction.auction.model.projection.AuctionFull
import ua.marchenko.artauction.auction.model.projection.AuctionFull.BidFull
import ua.marchenko.artauction.common.mongodb.id.toObjectId
import ua.marchenko.artauction.user.model.MongoUser
import user.random

fun MongoAuction.Companion.random(
    id: String? = getRandomObjectId().toHexString(),
    artwork: MongoArtwork? = MongoArtwork.random(),
    startBid: Double? = 100.0,
) = MongoAuction(
    id = id?.toObjectId(),
    artworkId = artwork?.id,
    startBid = startBid,
    startedAt = LocalDateTime.now(),
    finishedAt = LocalDateTime.now().plusDays(1),
)

fun MongoAuction.Bid.Companion.random() = MongoAuction.Bid(
    buyerId = getRandomObjectId(),
    bid = 100.0,
)

fun CreateAuctionRequest.Companion.random(artworkId: String = getRandomObjectId().toHexString()) =
    CreateAuctionRequest(
        artworkId = artworkId,
        startBid = 100.0,
        startedAt = LocalDateTime.now(),
        finishedAt = LocalDateTime.now(),
    )

fun AuctionFull.Companion.random(buyers: List<BidFull>? = listOf(BidFull.random(), BidFull.random())) = AuctionFull(
    id = getRandomObjectId(),
    artwork = ArtworkFull.random(),
    startBid = 100.0,
    buyers = buyers,
    startedAt = LocalDateTime.now(),
    finishedAt = LocalDateTime.now().plusDays(1),
)

fun BidFull.Companion.random() = BidFull(
    buyer = MongoUser.random(),
    bid = 100.0,
)
