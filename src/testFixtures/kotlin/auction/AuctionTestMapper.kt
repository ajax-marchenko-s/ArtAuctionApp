package auction

import ua.marchenko.artauction.artwork.model.projection.ArtworkFull
import ua.marchenko.artauction.auction.model.MongoAuction
import ua.marchenko.artauction.auction.model.projection.AuctionFull
import ua.marchenko.artauction.auction.model.projection.AuctionFull.BidFull
import ua.marchenko.artauction.user.model.MongoUser

fun MongoAuction.toFullAuction(artwork: ArtworkFull? = null, buyers: List<BidFull>? = null) = AuctionFull(
    id = id,
    artwork = artwork,
    startBid = startBid,
    buyers = buyers,
    startedAt = startedAt,
    finishedAt = finishedAt,
)

fun MongoAuction.Bid.toFullBid(buyer: MongoUser? = null) = BidFull(
    buyer = buyer,
    bid = bid,
)
