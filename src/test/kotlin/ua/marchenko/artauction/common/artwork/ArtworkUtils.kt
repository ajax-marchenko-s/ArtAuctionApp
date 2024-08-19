package ua.marchenko.artauction.common.artwork

import ua.marchenko.artauction.artwork.controller.dto.ArtworkRequest
import ua.marchenko.artauction.artwork.enums.ArtworkStatus
import ua.marchenko.artauction.artwork.enums.ArtworkStyle
import ua.marchenko.artauction.artwork.model.Artwork
import ua.marchenko.artauction.common.getRandomString
import ua.marchenko.artauction.common.user.getRandomUser
import ua.marchenko.artauction.user.enums.Role
import ua.marchenko.artauction.user.model.User

fun getRandomArtwork(
    id: String = getRandomString(),
    status: ArtworkStatus? = ArtworkStatus.VIEW,
    artist: User? = getRandomUser(role = Role.ARTIST)
): Artwork {
    return Artwork(
        id = id,
        title = getRandomString(),
        description = getRandomString(),
        style = ArtworkStyle.POP_ART,
        width = 100,
        height = 100,
        status = status,
        artist = artist
    )
}

fun getRandomArtworkRequest(): ArtworkRequest {
    return ArtworkRequest(
        title = getRandomString(),
        description = getRandomString(),
        style = ArtworkStyle.POP_ART,
        width = 100,
        height = 100
    )
}
