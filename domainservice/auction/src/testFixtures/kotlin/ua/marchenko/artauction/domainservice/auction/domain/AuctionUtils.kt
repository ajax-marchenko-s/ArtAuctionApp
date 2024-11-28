package ua.marchenko.artauction.domainservice.auction.domain

import java.math.BigDecimal
import java.time.LocalDateTime
import kotlin.random.Random
import org.bson.types.ObjectId
import ua.marchenko.artauction.domainservice.auction.domain.projection.AuctionFull
import ua.marchenko.artauction.domainservice.auction.domain.projection.AuctionFull.BidFull
import ua.marchenko.artauction.domainservice.user.domain.User
import ua.marchenko.artauction.domainservice.artwork.domain.projection.ArtworkFull
import ua.marchenko.artauction.domainservice.artwork.domain.random
import ua.marchenko.artauction.domainservice.user.domain.random

fun Auction.Companion.random(
    id: String? = ObjectId().toHexString(),
    artworkId: String = ObjectId().toHexString(),
    startBid: BigDecimal = BigDecimal(Random.nextInt(10, 100)),
    startedAt: LocalDateTime = LocalDateTime.now(),
    finishedAt: LocalDateTime = LocalDateTime.now().plusDays(1),
    buyers: List<Auction.Bid> = emptyList()
) = Auction(
    id = id,
    artworkId = artworkId,
    startBid = startBid,
    startedAt = startedAt,
    finishedAt = finishedAt,
    buyers = buyers,
)

fun Auction.Bid.Companion.random(
    buyerId: String = ObjectId().toHexString(),
    bid: BigDecimal = BigDecimal(Random.nextInt(10, 100))
) =
    Auction.Bid(
        buyerId = buyerId,
        bid = bid,
    )

fun AuctionFull.Companion.random(
    id: String = ObjectId().toHexString(),
    buyers: List<BidFull> = listOf(BidFull.random(), BidFull.random())
) = AuctionFull(
    id = id,
    artwork = ArtworkFull.random(),
    startBid = BigDecimal(Random.nextInt(10, 100)),
    buyers = buyers,
    startedAt = LocalDateTime.now(),
    finishedAt = LocalDateTime.now().plusDays(1),
)

fun BidFull.Companion.random() = BidFull(
    buyer = User.random(),
    bid = BigDecimal(Random.nextInt(10, 100)),
)
