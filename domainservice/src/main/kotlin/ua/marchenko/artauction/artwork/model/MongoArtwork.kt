package ua.marchenko.artauction.artwork.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId
import ua.marchenko.core.artwork.enums.ArtworkStatus
import ua.marchenko.core.artwork.enums.ArtworkStyle

@Document(collection = MongoArtwork.COLLECTION)
@TypeAlias("Artwork")
data class MongoArtwork(
    @MongoId
    val id: ObjectId? = null,
    val title: String? = null,
    val description: String? = null,
    val style: ArtworkStyle? = null,
    val width: Int? = null,
    val height: Int? = null,
    val status: ArtworkStatus? = null,
    val artistId: ObjectId? = null,
) {
    companion object {
        const val COLLECTION = "artwork"
    }
}
