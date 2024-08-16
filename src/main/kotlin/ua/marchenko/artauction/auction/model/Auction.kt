package ua.marchenko.artauction.auction.model

import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.FieldType
import org.springframework.data.mongodb.core.mapping.MongoId
import ua.marchenko.artauction.artwork.model.Artwork
import ua.marchenko.artauction.user.model.User
import java.time.LocalDateTime

@Document(collection = "auction")
data class Auction(
    @MongoId(FieldType.OBJECT_ID)
    val id: String? = null,
    @DBRef
    val artwork: Artwork? = null,
    val bid: Double? = null,
    @DBRef
    val buyer: User? = null,
    val startedAt: LocalDateTime? = null,
    val finishedAt: LocalDateTime? = null,
)
