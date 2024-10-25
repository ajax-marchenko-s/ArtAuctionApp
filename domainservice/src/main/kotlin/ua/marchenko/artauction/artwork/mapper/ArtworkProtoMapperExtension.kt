@file:Suppress("TooManyFunctions")

package ua.marchenko.artauction.artwork.mapper

import ua.marchenko.artauction.artwork.model.MongoArtwork
import ua.marchenko.artauction.artwork.model.projection.ArtworkFull
import ua.marchenko.artauction.common.mongodb.id.toObjectId
import ua.marchenko.core.artwork.exception.ArtworkNotFoundException
import ua.marchenko.artauction.user.mapper.toUserProto
import ua.marchenko.artauction.user.model.MongoUser
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

fun CreateArtworkRequestProto.toMongo(): MongoArtwork = MongoArtwork(
    id = null,
    title = title,
    description = description,
    style = style.toArtworkStyle(),
    width = width,
    height = height,
    status = null,
    artistId = artistId.toObjectId()
)

fun MongoArtwork.toCreateArtworkSuccessResponseProto(): CreateArtworkResponseProto {
    return CreateArtworkResponseProto.newBuilder().also { builder ->
        builder.successBuilder.setArtwork(toArtworkProto())
    }.build()
}

fun Throwable.toCreateArtworkFailureResponseProto(): CreateArtworkResponseProto {
    return CreateArtworkResponseProto.newBuilder().also { builder ->
        builder.failureBuilder.message = message.orEmpty()
        if (this is UserNotFoundException) {
            builder.failureBuilder.artistNotFoundBuilder
        }
    }.build()
}

fun MongoArtwork.toFindArtworkByIdSuccessResponseProto(): FindArtworkByIdResponseProto {
    return FindArtworkByIdResponseProto.newBuilder().also { builder ->
        builder.successBuilder.setArtwork(toArtworkProto())
    }.build()
}

fun Throwable.toFindArtworkByIdFailureResponseProto(): FindArtworkByIdResponseProto {
    return FindArtworkByIdResponseProto.newBuilder().also { builder ->
        builder.failureBuilder.message = message.orEmpty()
        if (this is ArtworkNotFoundException) {
            builder.failureBuilder.notFoundByIdBuilder
        }
    }.build()
}

fun List<MongoArtwork>.toFindAllArtworksSuccessResponseProto(): FindAllArtworksResponseProto {
    return FindAllArtworksResponseProto.newBuilder().also { builder ->
        builder.successBuilder.addAllArtworks(map { it.toArtworkProto() })
    }.build()
}

fun Throwable.toFindAllArtworksFailureResponseProto(): FindAllArtworksResponseProto {
    return FindAllArtworksResponseProto.newBuilder().also { builder ->
        builder.failureBuilder.message = message.orEmpty()
    }.build()
}

fun ArtworkFull.toFindArtworkFullByIdSuccessResponseProto(): FindArtworkFullByIdResponseProto {
    return FindArtworkFullByIdResponseProto.newBuilder().also { builder ->
        builder.successBuilder.setArtwork(toArtworkFullProto())
    }.build()
}

fun Throwable.toFindArtworkFullByIdFailureResponseProto(): FindArtworkFullByIdResponseProto {
    return FindArtworkFullByIdResponseProto.newBuilder().also { builder ->
        builder.failureBuilder.message = message.orEmpty()
        if (this is ArtworkNotFoundException) {
            builder.failureBuilder.notFoundByIdBuilder
        }
    }.build()
}

fun List<ArtworkFull>.toFindAllArtworksFullSuccessResponseProto(): FindAllArtworksFullResponseProto {
    return FindAllArtworksFullResponseProto.newBuilder().also { builder ->
        builder.successBuilder.addAllArtworks(this.map { it.toArtworkFullProto() })
    }.build()
}

fun Throwable.toFindAllArtworksFullFailureResponseProto(): FindAllArtworksFullResponseProto {
    return FindAllArtworksFullResponseProto.newBuilder().also { builder ->
        builder.failureBuilder.message = message.orEmpty()
    }.build()
}

fun ArtworkFull.toArtworkFullProto(): ArtworkFullProto {
    return ArtworkFullProto.newBuilder().also {
        it.id = requireNotNull(id) { "artwork id cannot be null" }.toHexString()
        it.title = title ?: "unknown"
        it.description = description ?: "unknown"
        it.width = width ?: 0
        it.height = height ?: 0
        it.style = (style ?: ArtworkStyle.UNKNOWN).toArtworkStyleProto()
        it.status = (status ?: ArtworkStatus.UNKNOWN).toArtworkStatusProto()
        it.artist = (artist ?: MongoUser()).toUserProto()
    }.build()
}

fun MongoArtwork.toArtworkProto(): ArtworkProto {
    return ArtworkProto.newBuilder().also {
        it.id = requireNotNull(id) { "artwork id cannot be null" }.toHexString()
        it.title = title ?: "unknown"
        it.description = description ?: "unknown"
        it.width = width ?: 0
        it.height = height ?: 0
        it.style = (style ?: ArtworkStyle.UNKNOWN).toArtworkStyleProto()
        it.status = (status ?: ArtworkStatus.UNKNOWN).toArtworkStatusProto()
        it.artistId = artistId?.toHexString() ?: "unknown"
    }
        .build()
}

private fun ArtworkStyleProto.toArtworkStyle(): ArtworkStyle {
    return when (this) {
        ArtworkStyleProto.ARTWORK_STYLE_UNSPECIFIED -> ArtworkStyle.UNKNOWN
        ArtworkStyleProto.ARTWORK_STYLE_REALISM -> ArtworkStyle.REALISM
        ArtworkStyleProto.ARTWORK_STYLE_IMPRESSIONISM -> ArtworkStyle.IMPRESSIONISM
        ArtworkStyleProto.ARTWORK_STYLE_EXPRESSIONISM -> ArtworkStyle.EXPRESSIONISM
        ArtworkStyleProto.ARTWORK_STYLE_CUBISM -> ArtworkStyle.CUBISM
        ArtworkStyleProto.ARTWORK_STYLE_SURREALISM -> ArtworkStyle.SURREALISM
        ArtworkStyleProto.ARTWORK_STYLE_ABSTRACT -> ArtworkStyle.ABSTRACT
        ArtworkStyleProto.ARTWORK_STYLE_POP_ART -> ArtworkStyle.POP_ART
        ArtworkStyleProto.ARTWORK_STYLE_MINIMALISM -> ArtworkStyle.MINIMALISM
        ArtworkStyleProto.ARTWORK_STYLE_RENAISSANCE -> ArtworkStyle.RENAISSANCE
        ArtworkStyleProto.UNRECOGNIZED -> ArtworkStyle.UNKNOWN
    }
}

private fun ArtworkStyle.toArtworkStyleProto(): ArtworkStyleProto {
    return when (this) {
        ArtworkStyle.UNKNOWN -> ArtworkStyleProto.ARTWORK_STYLE_UNSPECIFIED
        ArtworkStyle.REALISM -> ArtworkStyleProto.ARTWORK_STYLE_REALISM
        ArtworkStyle.IMPRESSIONISM -> ArtworkStyleProto.ARTWORK_STYLE_IMPRESSIONISM
        ArtworkStyle.EXPRESSIONISM -> ArtworkStyleProto.ARTWORK_STYLE_EXPRESSIONISM
        ArtworkStyle.CUBISM -> ArtworkStyleProto.ARTWORK_STYLE_CUBISM
        ArtworkStyle.SURREALISM -> ArtworkStyleProto.ARTWORK_STYLE_SURREALISM
        ArtworkStyle.ABSTRACT -> ArtworkStyleProto.ARTWORK_STYLE_ABSTRACT
        ArtworkStyle.POP_ART -> ArtworkStyleProto.ARTWORK_STYLE_POP_ART
        ArtworkStyle.MINIMALISM -> ArtworkStyleProto.ARTWORK_STYLE_MINIMALISM
        ArtworkStyle.RENAISSANCE -> ArtworkStyleProto.ARTWORK_STYLE_RENAISSANCE
    }
}

private fun ArtworkStatus.toArtworkStatusProto(): ArtworkStatusProto {
    return when (this) {
        ArtworkStatus.VIEW -> ArtworkStatusProto.ARTWORK_STATUS_VIEW
        ArtworkStatus.UNKNOWN -> ArtworkStatusProto.ARTWORK_STATUS_UNSPECIFIED
        ArtworkStatus.SOLD -> ArtworkStatusProto.ARTWORK_STATUS_SOLD
        ArtworkStatus.ON_AUCTION -> ArtworkStatusProto.ARTWORK_STATUS_ON_AUCTION
    }
}
