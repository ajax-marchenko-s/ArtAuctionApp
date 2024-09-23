package artwork

import getRandomObjectId
import ua.marchenko.artauction.artwork.controller.dto.CreateArtworkRequest
import ua.marchenko.artauction.artwork.enums.ArtworkStatus
import ua.marchenko.artauction.artwork.enums.ArtworkStyle
import ua.marchenko.artauction.artwork.model.MongoArtwork
import getRandomString
import ua.marchenko.artauction.artwork.model.projection.ArtworkFull
import ua.marchenko.artauction.common.mongodb.id.toObjectId
import ua.marchenko.artauction.user.model.MongoUser
import user.random

fun MongoArtwork.Companion.random(
    id: String? = getRandomObjectId().toHexString(),
    status: ArtworkStatus? = ArtworkStatus.VIEW,
    artistId: String? = getRandomObjectId().toHexString(),
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

fun CreateArtworkRequest.Companion.random() =
    CreateArtworkRequest(
        title = getRandomString(),
        description = getRandomString(),
        style = ArtworkStyle.POP_ART,
        width = 100,
        height = 100,
    )

fun ArtworkFull.Companion.random( status: ArtworkStatus? = ArtworkStatus.VIEW) = ArtworkFull(
    id = getRandomObjectId(),
    title = getRandomString(),
    description = getRandomString(),
    style = ArtworkStyle.POP_ART,
    width = 100,
    height = 100,
    status = status,
    artist = MongoUser.random()
)
