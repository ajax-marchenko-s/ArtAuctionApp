package ua.marchenko.artauction.domainservice.auction.infrastructure.mongo.entity

import java.math.BigDecimal
import java.time.LocalDateTime
import org.bson.types.ObjectId
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId

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
