package artwork

import getRandomObjectId
import ua.marchenko.artauction.artwork.controller.dto.CreateArtworkRequest
import ua.marchenko.artauction.artwork.enums.ArtworkStatus
import ua.marchenko.artauction.artwork.enums.ArtworkStyle
import ua.marchenko.artauction.artwork.model.Artwork
import getRandomString
import ua.marchenko.artauction.common.mongodb.id.toObjectId
import ua.marchenko.artauction.user.enums.Role
import ua.marchenko.artauction.user.model.User
import user.random

fun Artwork.Companion.random(
    id: String? = getRandomObjectId().toHexString(),
    status: ArtworkStatus? = ArtworkStatus.VIEW,
    artist: User? = User.random(role = Role.ARTIST),
) = Artwork(
    id = id?.toObjectId(),
    title = getRandomString(),
    description = getRandomString(),
    style = ArtworkStyle.POP_ART,
    width = 100,
    height = 100,
    status = status,
    artist = artist,
)

fun CreateArtworkRequest.Companion.random() =
    CreateArtworkRequest(
        title = getRandomString(),
        description = getRandomString(),
        style = ArtworkStyle.POP_ART,
        width = 100,
        height = 100,
    )
