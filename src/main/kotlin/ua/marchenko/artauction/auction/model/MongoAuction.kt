package ua.marchenko.artauction.auction.model

import java.math.BigDecimal
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId

import java.time.LocalDateTime
import org.bson.types.ObjectId
import org.springframework.data.annotation.TypeAlias

@Document(collection = MongoAuction.COLLECTION)
@TypeAlias("Auction")
data class MongoAuction(
    @MongoId
    val id: ObjectId? = null,
    val artworkId: ObjectId? = null,
    val startBid: BigDecimal? = null,
    val buyers: List<Bid>? = emptyList(),
    val startedAt: LocalDateTime? = null,
    val finishedAt: LocalDateTime? = null,
) {
    data class Bid(
        val buyerId: ObjectId? = null,
        val bid: BigDecimal? = null,
    ) {
        companion object
    }

    companion object {
        const val COLLECTION = "auction"
    }
}
