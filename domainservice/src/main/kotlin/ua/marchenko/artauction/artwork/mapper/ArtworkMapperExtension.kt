package ua.marchenko.artauction.artwork.mapper

import ua.marchenko.artauction.artwork.model.projection.ArtworkFull
import ua.marchenko.artauction.user.mapper.toResponse
import ua.marchenko.artauction.user.model.MongoUser
import ua.marchenko.core.artwork.dto.ArtworkFullResponse

fun ArtworkFull.toFullResponse(): ArtworkFullResponse = ArtworkFullResponse(
    requireNotNull(id) { "artwork id cannot be null" }.toHexString(),
    title ?: "unknown",
    description ?: "unknown",
    requireNotNull(style) { "artwork style cannot be null" },
    width ?: 0,
    height ?: 0,
    requireNotNull(status) { "artwork status cannot be null" },
    artist?.toResponse() ?: MongoUser().toResponse()
)
