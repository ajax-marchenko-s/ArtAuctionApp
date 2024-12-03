package ua.marchenko.artauction.domainservice.auction.infrastructure.mongo.entity.projection

import java.math.BigDecimal
import java.time.LocalDateTime
import org.bson.types.ObjectId
import ua.marchenko.artauction.domainservice.user.infrastructure.mongo.entity.MongoUser
import ua.marchenko.artauction.domainservice.artwork.infrastructure.mongo.entity.projection.MongoArtworkFull

data class MongoAuctionFull(
    val id: ObjectId? = null,
    val artwork: MongoArtworkFull? = null,
    val startBid: BigDecimal? = null,
    val buyers: List<BidFull>? = emptyList(),
    val startedAt: LocalDateTime? = null,
    val finishedAt: LocalDateTime? = null,
) {
    data class BidFull(
        val buyer: MongoUser? = null,
        val bid: BigDecimal? = null,
    ) {
        companion object
    }

    companion object
}
