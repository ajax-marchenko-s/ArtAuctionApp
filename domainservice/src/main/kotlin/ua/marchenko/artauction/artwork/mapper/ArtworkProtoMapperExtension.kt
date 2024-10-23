@file:Suppress("TooManyFunctions")

package ua.marchenko.artauction.artwork.mapper

import ua.marchenko.core.artwork.exception.ArtworkNotFoundException
import ua.marchenko.artauction.user.mapper.toUserProto
import ua.marchenko.core.artwork.dto.ArtworkFullResponse
import ua.marchenko.core.artwork.dto.ArtworkResponse
import ua.marchenko.core.artwork.dto.CreateArtworkRequest
import ua.marchenko.core.artwork.enums.ArtworkStatus
import ua.marchenko.core.artwork.enums.ArtworkStyle
import ua.marchenko.core.user.exception.UserNotFoundException
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksResponse as FindAllArtworksResponseProto
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksFullResponse as FindAllArtworksFullResponseProto
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkByIdResponse as FindArtworkByIdResponseProto
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkFullByIdResponse as FindArtworkFullByIdResponseProto
import ua.marchenko.internal.commonmodels.artwork.ArtworkFull as ArtworkFullProto
import ua.marchenko.internal.commonmodels.artwork.ArtworkStyle as ArtworkStyleProto
import ua.marchenko.internal.commonmodels.artwork.ArtworkStatus as ArtworkStatusProto
import ua.marchenko.internal.input.reqreply.artwork.CreateArtworkResponse as CreateArtworkResponseProto
import ua.marchenko.internal.input.reqreply.artwork.CreateArtworkRequest as CreateArtworkRequestProto
import ua.marchenko.internal.commonmodels.artwork.Artwork as ArtworkProto

fun CreateArtworkRequestProto.toCreateArtworkRequest(): CreateArtworkRequest =
    CreateArtworkRequest(title, description, style.toArtworkStyle(), width, height, artistId)

fun ArtworkResponse.toCreateArtworkSuccessResponseProto(): CreateArtworkResponseProto {
    return CreateArtworkResponseProto.newBuilder().also { builder ->
        builder.successBuilder.setArtwork(this.toArtworkProto())
    }.build()
}

fun Throwable.toCreateArtworkFailureResponseProto(): CreateArtworkResponseProto {
    return CreateArtworkResponseProto.newBuilder().also { builder ->
        builder.failureBuilder.setMessage(this.message.orEmpty())
        if (this is UserNotFoundException) {
            builder.failureBuilder.artistNotFoundBuilder
        }
    }.build()
}

fun ArtworkResponse.toFindArtworkByIdSuccessResponseProto(): FindArtworkByIdResponseProto {
    return FindArtworkByIdResponseProto.newBuilder().also { builder ->
        builder.successBuilder.setArtwork(this.toArtworkProto())
    }.build()
}

fun Throwable.toFindArtworkByIdFailureResponseProto(): FindArtworkByIdResponseProto {
    return FindArtworkByIdResponseProto.newBuilder().also { builder ->
        builder.failureBuilder.setMessage(message.orEmpty())
        if (this is ArtworkNotFoundException) {
            builder.failureBuilder.notFoundByIdBuilder
        }
    }.build()
}

fun List<ArtworkResponse>.toFindAllArtworksSuccessResponseProto(): FindAllArtworksResponseProto {
    return FindAllArtworksResponseProto.newBuilder().also { builder ->
        builder.successBuilder.addAllArtworks(this.map { it.toArtworkProto() })
    }.build()
}

fun Throwable.toFindAllArtworksFailureResponseProto(): FindAllArtworksResponseProto {
    return FindAllArtworksResponseProto.newBuilder().also { builder ->
        builder.failureBuilder.setMessage(message.orEmpty())
    }.build()
}

fun ArtworkFullResponse.toFindArtworkFullByIdSuccessResponseProto(): FindArtworkFullByIdResponseProto {
    return FindArtworkFullByIdResponseProto.newBuilder().also { builder ->
        builder.successBuilder.setArtwork(this.toArtworkFullProto())
    }.build()
}

fun Throwable.toFindArtworkFullByIdFailureResponseProto(): FindArtworkFullByIdResponseProto {
    return FindArtworkFullByIdResponseProto.newBuilder().also { builder ->
        builder.failureBuilder.setMessage(message.orEmpty())
        if (this is ArtworkNotFoundException) {
            builder.failureBuilder.notFoundByIdBuilder
        }
    }.build()
}

fun List<ArtworkFullResponse>.toFindAllArtworksFullSuccessResponseProto(): FindAllArtworksFullResponseProto {
    return FindAllArtworksFullResponseProto.newBuilder().also { builder ->
        builder.successBuilder.addAllArtworks(this.map { it.toArtworkFullProto() })
    }.build()
}

fun Throwable.toFindAllArtworksFullFailureResponseProto(): FindAllArtworksFullResponseProto {
    return FindAllArtworksFullResponseProto.newBuilder().also { builder ->
        builder.failureBuilder.setMessage(message.orEmpty())
    }.build()
}

fun ArtworkFullResponse.toArtworkFullProto(): ArtworkFullProto {
    return ArtworkFullProto.newBuilder()
        .setId(id)
        .setTitle(title)
        .setDescription(description)
        .setWidth(width)
        .setHeight(height)
        .setStyle(style.toArtworkStyleProto())
        .setStatus(status.toArtworkStatusProto())
        .setArtist(artist.toUserProto())
        .build()
}

fun ArtworkResponse.toArtworkProto(): ArtworkProto {
    return ArtworkProto.newBuilder()
        .setId(id)
        .setTitle(title)
        .setDescription(description)
        .setArtistId(artistId)
        .setWidth(width)
        .setHeight(height)
        .setStyle(style.toArtworkStyleProto())
        .setStatus(status.toArtworkStatusProto())
        .build()
}

private fun ArtworkStyleProto.toArtworkStyle(): ArtworkStyle = runCatching { ArtworkStyle.valueOf(this.name) }
    .getOrDefault(ArtworkStyle.UNKNOWN)

private fun ArtworkStyle.toArtworkStyleProto(): ArtworkStyleProto = runCatching { ArtworkStyleProto.valueOf(this.name) }
    .getOrDefault(ArtworkStyleProto.ARTWORK_STYLE_UNSPECIFIED)

private fun ArtworkStatus.toArtworkStatusProto(): ArtworkStatusProto =
    runCatching { ArtworkStatusProto.valueOf(this.name) }
        .getOrDefault(ArtworkStatusProto.ARTWORK_STATUS_UNSPECIFIED)
