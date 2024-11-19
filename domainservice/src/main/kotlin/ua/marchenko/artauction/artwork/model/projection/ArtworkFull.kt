package ua.marchenko.artauction.artwork.model.projection

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import org.bson.types.ObjectId
import ua.marchenko.artauction.user.model.MongoUser
import ua.marchenko.core.artwork.enums.ArtworkStatus
import ua.marchenko.core.artwork.enums.ArtworkStyle

data class ArtworkFull(
    @JsonSerialize(using = ToStringSerializer::class)
    val id: ObjectId? = null,
    val title: String? = null,
    val description: String? = null,
    val style: ArtworkStyle? = null,
    val width: Int? = null,
    val height: Int? = null,
    val status: ArtworkStatus? = null,
    val artist: MongoUser? = null,
){
    companion object
}
