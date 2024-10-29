package artwork

import getRandomInt
import getRandomString
import ua.marchenko.artauction.artwork.model.MongoArtwork
import org.bson.types.ObjectId
import ua.marchenko.artauction.artwork.model.projection.ArtworkFull
import ua.marchenko.artauction.common.mongodb.id.toObjectId
import ua.marchenko.artauction.user.model.MongoUser
import ua.marchenko.core.artwork.enums.ArtworkStatus
import ua.marchenko.core.artwork.enums.ArtworkStyle
import user.random

fun MongoArtwork.Companion.random(
    id: String? = ObjectId().toHexString(),
    status: ArtworkStatus? = ArtworkStatus.VIEW,
    artistId: String? = ObjectId().toHexString(),
    style: ArtworkStyle? = ArtworkStyle.POP_ART,
) = MongoArtwork(
    id = id?.toObjectId(),
    title = getRandomString(),
    description = getRandomString(),
    style = style,
    width = getRandomInt(),
    height = getRandomInt(),
    status = status,
    artistId = artistId?.toObjectId(),
)

fun ArtworkFull.Companion.random(
    id: String? = ObjectId().toHexString(),
    status: ArtworkStatus? = ArtworkStatus.VIEW
) = ArtworkFull(
    id = id?.toObjectId(),
    title = getRandomString(),
    description = getRandomString(),
    style = ArtworkStyle.POP_ART,
    width = getRandomInt(),
    height = getRandomInt(),
    status = status,
    artist = MongoUser.random()
)
