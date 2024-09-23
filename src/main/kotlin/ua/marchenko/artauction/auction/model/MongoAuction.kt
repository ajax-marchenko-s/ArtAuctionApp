package ua.marchenko.artauction.auction.model

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId

import java.time.LocalDateTime
import org.bson.types.ObjectId

@Document(collection = "auction")
data class MongoAuction(
    @MongoId
    val id: ObjectId? = null,
    val artworkId: ObjectId? = null,
    val startBid: Double? = null,
    val buyers: List<Bid>? = emptyList(),
    val startedAt: LocalDateTime? = null,
    val finishedAt: LocalDateTime? = null,
) {
    data class Bid(
        val buyerId: ObjectId? = null,
        val bid: Double? = null,
    )
    companion object
}
