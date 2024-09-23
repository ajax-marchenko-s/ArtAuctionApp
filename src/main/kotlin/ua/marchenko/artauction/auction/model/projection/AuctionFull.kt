package ua.marchenko.artauction.auction.model.projection

import java.time.LocalDateTime
import org.bson.types.ObjectId
import ua.marchenko.artauction.artwork.model.projection.ArtworkFull
import ua.marchenko.artauction.user.model.MongoUser

data class AuctionFull(
    val id: ObjectId? = null,
    val artwork: ArtworkFull? = null,
    val startBid: Double? = null,
    val buyers: List<BidFull>? = emptyList(),
    val startedAt: LocalDateTime? = null,
    val finishedAt: LocalDateTime? = null,
) {
    data class BidFull(
        val buyer: MongoUser? = null,
        val bid: Double? = null,
    ){
        companion object
    }
    companion object
}
