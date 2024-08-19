package ua.marchenko.artauction.artwork.mapper

import ua.marchenko.artauction.artwork.controller.dto.ArtworkRequest
import ua.marchenko.artauction.artwork.controller.dto.ArtworkResponse
import ua.marchenko.artauction.artwork.model.Artwork
import ua.marchenko.artauction.user.mapper.toUserResponse

fun Artwork.toArtworkResponse() = ArtworkResponse(
    id ?: throwIllegalArgumentException("id"),
    title ?: throwIllegalArgumentException("title"),
    description ?: throwIllegalArgumentException("description"),
    style ?: throwIllegalArgumentException("style"),
    width ?: throwIllegalArgumentException("width"),
    height ?: throwIllegalArgumentException("height"),
    status ?: throwIllegalArgumentException("status"),
    artist?.toUserResponse() ?: throwIllegalArgumentException("artist")
)

fun ArtworkRequest.toArtwork() = Artwork(null, title, description, style, width, height, null, null)

private fun throwIllegalArgumentException(field: String): Nothing {
    throw IllegalArgumentException("Artwork entity is in an invalid state: missing required field: $field")
}
