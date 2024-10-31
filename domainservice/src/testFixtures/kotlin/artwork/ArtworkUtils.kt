package artwork

import getRandomString
import kotlin.random.Random
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
    width = Random.nextInt(10, 100),
    height = Random.nextInt(10, 100),
    status = status,
    artistId = artistId?.toObjectId(),
)

fun ArtworkFull.Companion.random(
    id: String? = ObjectId().toHexString(),
    status: ArtworkStatus? = ArtworkStatus.VIEW,
    title: String? = getRandomString(),
) = ArtworkFull(
    id = id?.toObjectId(),
    title = title,
    description = getRandomString(),
    style = ArtworkStyle.POP_ART,
    width = Random.nextInt(10, 100),
    height = Random.nextInt(10, 100),
    status = status,
    artist = MongoUser.random()
)
