package ua.marchenko.artauction.artwork.mapper

import ua.marchenko.artauction.artwork.controller.dto.ArtworkFullResponse
import ua.marchenko.artauction.artwork.controller.dto.CreateArtworkRequest
import ua.marchenko.artauction.artwork.controller.dto.ArtworkResponse
import ua.marchenko.artauction.artwork.enums.ArtworkStatus
import ua.marchenko.artauction.artwork.enums.ArtworkStyle
import ua.marchenko.artauction.artwork.model.MongoArtwork
import ua.marchenko.artauction.artwork.model.projection.ArtworkFull
import ua.marchenko.artauction.user.mapper.toUserResponse
import ua.marchenko.artauction.user.model.MongoUser

fun MongoArtwork.toArtworkResponse() = ArtworkResponse(
    requireNotNull(id) { "artwork id cannot be null" }.toHexString(),
    title ?: "unknown",
    description ?: "unknown",
    style ?: ArtworkStyle.UNKNOWN,
    width ?: 0,
    height ?: 0,
    status ?: ArtworkStatus.UNKNOWN,
    artistId?.toHexString() ?: "unknown",
)

fun ArtworkFull.toArtworkFullResponse() = ArtworkFullResponse(
    requireNotNull(id) { "artwork id cannot be null" }.toHexString(),
    title ?: "unknown",
    description ?: "unknown",
    style ?: ArtworkStyle.UNKNOWN,
    width ?: 0,
    height ?: 0,
    status ?: ArtworkStatus.UNKNOWN,
    artist?.toUserResponse() ?: MongoUser().toUserResponse()
)

fun CreateArtworkRequest.toArtwork() = MongoArtwork(null, title, description, style, width, height, null, null)
