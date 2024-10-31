package auction

import ua.marchenko.artauction.auction.controller.dto.CreateAuctionRequest
import ua.marchenko.artauction.auction.model.MongoAuction
import artwork.random
import java.math.BigDecimal
import java.time.LocalDateTime
import kotlin.random.Random
import org.bson.types.ObjectId
import ua.marchenko.artauction.artwork.model.MongoArtwork
import ua.marchenko.artauction.artwork.model.projection.ArtworkFull
import ua.marchenko.artauction.auction.model.MongoAuction.Bid
import ua.marchenko.artauction.auction.model.projection.AuctionFull
import ua.marchenko.artauction.auction.model.projection.AuctionFull.BidFull
import ua.marchenko.artauction.common.mongodb.id.toObjectId
import ua.marchenko.artauction.user.model.MongoUser
import user.random

fun MongoAuction.Companion.random(
    id: String? = ObjectId().toHexString(),
    artwork: MongoArtwork? = MongoArtwork.random(),
    startBid: BigDecimal? = BigDecimal(100.0),
    startedAt: LocalDateTime? = LocalDateTime.now(),
    finishedAt: LocalDateTime? = LocalDateTime.now().plusDays(1),
    buyers: List<Bid>? = emptyList()
) = MongoAuction(
    id = id?.toObjectId(),
    artworkId = artwork?.id,
    startBid = startBid,
    startedAt = startedAt,
    finishedAt = finishedAt,
    buyers = buyers,
)

fun Bid.Companion.random(
    buyerId: String? = ObjectId().toHexString(),
    bid: BigDecimal? = BigDecimal(Random.nextInt(10, 100))
) =
    Bid(
        buyerId = buyerId?.toObjectId(),
        bid = bid,
    )

fun CreateAuctionRequest.Companion.random(artworkId: String = ObjectId().toHexString()) =
    CreateAuctionRequest(
        artworkId = artworkId,
        startBid = BigDecimal(Random.nextInt(10, 100)),
        startedAt = LocalDateTime.now(),
        finishedAt = LocalDateTime.now(),
    )

fun AuctionFull.Companion.random(
    id: String? = ObjectId().toHexString(),
    buyers: List<BidFull>? = listOf(BidFull.random(), BidFull.random())
) = AuctionFull(
    id = id?.toObjectId(),
    artwork = ArtworkFull.random(),
    startBid = BigDecimal(Random.nextInt(10, 100)),
    buyers = buyers,
    startedAt = LocalDateTime.now(),
    finishedAt = LocalDateTime.now().plusDays(1),
)

fun BidFull.Companion.random() = BidFull(
    buyer = MongoUser.random(),
    bid = BigDecimal(Random.nextInt(10, 100)),
)
