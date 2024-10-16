package artwork

import ua.marchenko.artauction.artwork.controller.dto.CreateArtworkRequest
import ua.marchenko.artauction.artwork.enums.ArtworkStatus
import ua.marchenko.artauction.artwork.enums.ArtworkStyle
import ua.marchenko.artauction.artwork.model.MongoArtwork
import getRandomString
import org.bson.types.ObjectId
import ua.marchenko.artauction.artwork.model.projection.ArtworkFull
import ua.marchenko.artauction.common.mongodb.id.toObjectId
import ua.marchenko.artauction.user.model.MongoUser
import user.random

fun MongoArtwork.Companion.random(
    id: String? = ObjectId().toHexString(),
    status: ArtworkStatus? = ArtworkStatus.VIEW,
    artistId: String? = ObjectId().toHexString(),
) = MongoArtwork(
    id = id?.toObjectId(),
    title = getRandomString(),
    description = getRandomString(),
    style = ArtworkStyle.POP_ART,
    width = 100,
    height = 100,
    status = status,
    artistId = artistId?.toObjectId(),
)

fun CreateArtworkRequest.Companion.random(title: String = getRandomString()) =
    CreateArtworkRequest(
        title = title,
        description = getRandomString(),
        style = ArtworkStyle.POP_ART,
        width = 100,
        height = 100,
    )

fun ArtworkFull.Companion.random(
    id: String? = ObjectId().toHexString(),
    status: ArtworkStatus? = ArtworkStatus.VIEW
) = ArtworkFull(
    id = id?.toObjectId(),
    title = getRandomString(),
    description = getRandomString(),
    style = ArtworkStyle.POP_ART,
    width = 100,
    height = 100,
    status = status,
    artist = MongoUser.random()
)
