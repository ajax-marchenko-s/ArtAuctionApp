package ua.marchenko.artauction.artwork.model

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId
import ua.marchenko.artauction.artwork.enums.ArtworkStatus
import ua.marchenko.artauction.artwork.enums.ArtworkStyle
import ua.marchenko.artauction.user.model.User

@Document(collection = "artwork")
data class Artwork(
    @MongoId
    val id: ObjectId? = null,
    val title: String? = null,
    val description: String? = null,
    val style: ArtworkStyle? = null,
    val width: Int? = null,
    val height: Int? = null,
    val status: ArtworkStatus? = null,
    @DBRef
    val artist: User? = null,
) {
    companion object
}
