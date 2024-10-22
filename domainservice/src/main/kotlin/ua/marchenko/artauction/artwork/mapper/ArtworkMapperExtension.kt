package ua.marchenko.artauction.artwork.mapper

import ua.marchenko.artauction.artwork.model.MongoArtwork
import ua.marchenko.artauction.artwork.model.projection.ArtworkFull
import ua.marchenko.artauction.user.mapper.toResponse
import ua.marchenko.artauction.user.model.MongoUser
import ua.marchenko.core.artwork.dto.ArtworkFullResponse
import ua.marchenko.core.artwork.dto.ArtworkResponse
import ua.marchenko.core.artwork.dto.CreateArtworkRequest
import ua.marchenko.core.artwork.enums.ArtworkStatus
import ua.marchenko.core.artwork.enums.ArtworkStyle

fun MongoArtwork.toResponse() = ArtworkResponse(
    requireNotNull(id) { "artwork id cannot be null" }.toHexString(),
    title ?: "unknown",
    description ?: "unknown",
    style ?: ArtworkStyle.UNKNOWN,
    width ?: 0,
    height ?: 0,
    status ?: ArtworkStatus.UNKNOWN,
    artistId?.toHexString() ?: "unknown",
)

fun ArtworkFull.toFullResponse() = ArtworkFullResponse(
    requireNotNull(id) { "artwork id cannot be null" }.toHexString(),
    title ?: "unknown",
    description ?: "unknown",
    style ?: ArtworkStyle.UNKNOWN,
    width ?: 0,
    height ?: 0,
    status ?: ArtworkStatus.UNKNOWN,
    artist?.toResponse() ?: MongoUser().toResponse()
)

fun CreateArtworkRequest.toMongo() = MongoArtwork(null, title, description, style, width, height, null, null)