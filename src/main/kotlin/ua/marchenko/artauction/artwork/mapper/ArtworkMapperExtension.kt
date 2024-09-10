package ua.marchenko.artauction.artwork.mapper

import ua.marchenko.artauction.artwork.controller.dto.CreateArtworkRequest
import ua.marchenko.artauction.artwork.controller.dto.ArtworkResponse
import ua.marchenko.artauction.artwork.enums.ArtworkStatus
import ua.marchenko.artauction.artwork.enums.ArtworkStyle
import ua.marchenko.artauction.artwork.model.Artwork
import ua.marchenko.artauction.user.mapper.toUserResponse
import ua.marchenko.artauction.user.model.User

fun Artwork.toArtworkResponse() = ArtworkResponse(
    requireNotNull(id) { "artwork id cannot be null" }.toHexString(),
    title ?: "unknown",
    description ?: "unknown",
    style ?: ArtworkStyle.UNKNOWN,
    width ?: 0,
    height ?: 0,
    status ?: ArtworkStatus.UNKNOWN,
    artist?.toUserResponse() ?: User().toUserResponse(),
)

fun CreateArtworkRequest.toArtwork() = Artwork(null, title, description, style, width, height, null, null)
