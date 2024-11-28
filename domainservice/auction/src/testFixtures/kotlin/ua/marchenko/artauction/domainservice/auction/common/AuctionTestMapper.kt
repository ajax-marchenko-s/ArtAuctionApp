package ua.marchenko.artauction.domainservice.auction.common

import ua.marchenko.artauction.domainservice.artwork.domain.projection.ArtworkFull
import ua.marchenko.artauction.domainservice.auction.domain.Auction
import ua.marchenko.artauction.domainservice.auction.domain.projection.AuctionFull
import ua.marchenko.artauction.domainservice.auction.domain.projection.AuctionFull.BidFull
import ua.marchenko.artauction.domainservice.user.domain.User

fun Auction.toFullAuction(artwork: ArtworkFull, buyers: List<BidFull> = emptyList()) = AuctionFull(
    id = id ?: "",
    artwork = artwork,
    startBid = startBid,
    buyers = buyers,
    startedAt = startedAt,
    finishedAt = finishedAt,
)

fun Auction.Bid.toFullBid(buyer: User) = BidFull(
    buyer = buyer,
    bid = bid,
)
