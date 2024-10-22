@file:Suppress("TooManyFunctions")

package ua.marchenko.gateway.artwork.mapper

import ua.marchenko.core.artwork.exception.ArtworkNotFoundException
import ua.marchenko.gateway.user.toUserResponse
import ua.marchenko.core.artwork.dto.ArtworkFullResponse
import ua.marchenko.core.artwork.dto.ArtworkResponse
import ua.marchenko.core.artwork.dto.CreateArtworkRequest
import ua.marchenko.core.artwork.enums.ArtworkStatus
import ua.marchenko.core.artwork.enums.ArtworkStyle
import ua.marchenko.internal.input.reqreply.artwork.CreateArtworkRequest as CreateArtworkRequestProto
import ua.marchenko.internal.commonmodels.artwork.ArtworkStyleProto
import ua.marchenko.internal.input.reqreply.artwork.CreateArtworkResponse as CreateArtworkResponseProto
import ua.marchenko.internal.commonmodels.artwork.Artwork as ArtworkProto
import ua.marchenko.internal.commonmodels.artwork.ArtworkFull as ArtworkFullProto
import ua.marchenko.internal.commonmodels.artwork.ArtworkStatus as ArtworkStatusProto
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkByIdResponse as FindArtworkByIdResponseProto
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkFullByIdResponse as FindArtworkFullByIdResponseProto
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksFullResponse as FindAllArtworksFullResponseProto
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksResponse as FindAllArtworksResponseProto

fun CreateArtworkRequest.toCreateArtworkRequestProto(): CreateArtworkRequestProto =
    CreateArtworkRequestProto.newBuilder()
        .setTitle(title)
        .setDescription(description)
        .setWidth(width)
        .setWidth(width)
        .setHeight(height)
        .setStyle(style.toArtworkStyleProto())
        .build()

fun CreateArtworkResponseProto.toArtworkResponse(): ArtworkResponse {
    if (hasFailure()) {
        error(failure.message)
    }
    return success.artwork.toArtworkResponse()
}

fun FindArtworkByIdResponseProto.toArtworkResponse(): ArtworkResponse {
    if (hasFailure()) {
        when (failure.errorCase!!) {
            FindArtworkByIdResponseProto.Failure.ErrorCase.NOT_FOUND_BY_ID ->
                throw ArtworkNotFoundException(failure.message)

            FindArtworkByIdResponseProto.Failure.ErrorCase.ERROR_NOT_SET ->
                error(failure.message)
        }
    }
    return success.artwork.toArtworkResponse()
}

fun FindAllArtworksResponseProto.toArtworksList(): List<ArtworkResponse> {
    if (hasFailure()) {
        error(failure.message)
    }
    return success.artworksList.map { it.toArtworkResponse() }
}

fun FindArtworkFullByIdResponseProto.toArtworkFullResponse(): ArtworkFullResponse {
    if (hasFailure()) {
        when (failure.errorCase!!) {
            FindArtworkFullByIdResponseProto.Failure.ErrorCase.NOT_FOUND_BY_ID ->
                throw ArtworkNotFoundException(failure.message)

            FindArtworkFullByIdResponseProto.Failure.ErrorCase.ERROR_NOT_SET ->
                error(failure.message)
        }
    }
    return success.artwork.toArtworkFullResponse()
}

fun FindAllArtworksFullResponseProto.toFullArtworkList(): List<ArtworkFullResponse> {
    if (hasFailure()) {
        error(failure.message)
    }
    return success.artworksList.map { it.toArtworkFullResponse() }
}

fun ArtworkProto.toArtworkResponse(): ArtworkResponse {
    return ArtworkResponse(
        id = id,
        title = title,
        description = description,
        style = style.toArtworkStyle(),
        width = width,
        height = height,
        status = status.toArtworkStatus(),
        artistId = artistId
    )
}

fun ArtworkFullProto.toArtworkFullResponse(): ArtworkFullResponse =
    ArtworkFullResponse(
        id = id,
        title = title,
        description = description,
        style = style.toArtworkStyle(),
        width = width,
        height = height,
        status = status.toArtworkStatus(),
        artist = artist.toUserResponse()
    )

fun ArtworkStyleProto.toArtworkStyle(): ArtworkStyle = runCatching { ArtworkStyle.valueOf(this.name) }
    .getOrDefault(ArtworkStyle.UNKNOWN)

fun ArtworkStyle.toArtworkStyleProto(): ArtworkStyleProto = runCatching { ArtworkStyleProto.valueOf(this.name) }
    .getOrDefault(ArtworkStyleProto.ARTWORK_STYLE_UNSPECIFIED)

fun ArtworkStatusProto.toArtworkStatus(): ArtworkStatus = runCatching { ArtworkStatus.valueOf(this.name) }
    .getOrDefault(ArtworkStatus.UNKNOWN)
