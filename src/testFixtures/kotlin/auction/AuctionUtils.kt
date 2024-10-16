package auction

import ua.marchenko.artauction.auction.controller.dto.CreateAuctionRequest
import ua.marchenko.artauction.auction.model.MongoAuction
import artwork.random
import java.math.BigDecimal
import java.time.LocalDateTime
import org.bson.types.ObjectId
import ua.marchenko.artauction.artwork.model.MongoArtwork
import ua.marchenko.artauction.artwork.model.projection.ArtworkFull
import ua.marchenko.artauction.auction.model.projection.AuctionFull
import ua.marchenko.artauction.auction.model.projection.AuctionFull.BidFull
import ua.marchenko.artauction.common.mongodb.id.toObjectId
import ua.marchenko.artauction.user.model.MongoUser
import user.random

fun MongoAuction.Companion.random(
    id: String? = ObjectId().toHexString(),
    artwork: MongoArtwork? = MongoArtwork.random(),
    startBid: BigDecimal? = BigDecimal(100.0),
    startedAt: LocalDateTime = LocalDateTime.now(),
    finishedAt: LocalDateTime = LocalDateTime.now().plusDays(1),
) = MongoAuction(
    id = id?.toObjectId(),
    artworkId = artwork?.id,
    startBid = startBid,
    startedAt = startedAt,
    finishedAt = finishedAt,
)

fun MongoAuction.Bid.Companion.random(
    buyerId: String? = ObjectId().toHexString(),
    bid: BigDecimal? = BigDecimal(100.0)
) =
    MongoAuction.Bid(
        buyerId = buyerId?.toObjectId(),
        bid = bid,
    )

fun CreateAuctionRequest.Companion.random(
    artworkId: String = ObjectId().toHexString(),
    startedAt: LocalDateTime = LocalDateTime.now(),
    finishedAt: LocalDateTime = LocalDateTime.now().plusDays(1),
) =
    CreateAuctionRequest(
        artworkId = artworkId,
        startBid = BigDecimal(100.0),
        startedAt = startedAt,
        finishedAt = finishedAt,
    )

fun AuctionFull.Companion.random(
    id: String? = ObjectId().toHexString(),
    buyers: List<BidFull>? = listOf(BidFull.random(), BidFull.random())
) = AuctionFull(
    id = id?.toObjectId(),
    artwork = ArtworkFull.random(),
    startBid = BigDecimal(100.0),
    buyers = buyers,
    startedAt = LocalDateTime.now(),
    finishedAt = LocalDateTime.now().plusDays(1),
)

fun BidFull.Companion.random() = BidFull(
    buyer = MongoUser.random(),
    bid = BigDecimal(100.0),
)
