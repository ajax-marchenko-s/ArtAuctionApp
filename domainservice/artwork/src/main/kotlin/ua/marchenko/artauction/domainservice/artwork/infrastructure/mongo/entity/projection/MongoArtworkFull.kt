package ua.marchenko.artauction.domainservice.artwork.infrastructure.mongo.entity.projection

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import org.bson.types.ObjectId
import ua.marchenko.artauction.domainservice.user.infrastructure.mongo.entity.MongoUser
import ua.marchenko.artauction.domainservice.artwork.infrastructure.mongo.entity.MongoArtwork.ArtworkStyle
import ua.marchenko.artauction.domainservice.artwork.infrastructure.mongo.entity.MongoArtwork.ArtworkStatus

data class MongoArtworkFull(
    @JsonSerialize(using = ToStringSerializer::class)
    val id: ObjectId? = null,
    val title: String? = null,
    val description: String? = null,
    val style: ArtworkStyle? = null,
    val width: Int? = null,
    val height: Int? = null,
    val status: ArtworkStatus? = null,
    val artist: MongoUser? = null,
) {
    companion object
}
