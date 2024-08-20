package artwork

import ua.marchenko.artauction.artwork.controller.dto.CreateArtworkRequest
import ua.marchenko.artauction.artwork.enums.ArtworkStatus
import ua.marchenko.artauction.artwork.enums.ArtworkStyle
import ua.marchenko.artauction.artwork.model.Artwork
import getRandomString
import user.getRandomUser
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

fun getRandomArtworkRequest(): CreateArtworkRequest {
    return CreateArtworkRequest(
        title = getRandomString(),
        description = getRandomString(),
        style = ArtworkStyle.POP_ART,
        width = 100,
        height = 100
    )
}
