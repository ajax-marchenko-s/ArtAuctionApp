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

fun ArtworkResponse.toFindArtworkByIdSuccessResponseProto(): FindArtworkByIdResponseProto {
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

fun List<ArtworkResponse>.toFindAllArtworksSuccessResponseProto(): FindAllArtworksResponseProto {
    return FindAllArtworksResponseProto.newBuilder().also { builder ->
        builder.successBuilder.addAllArtworks(map { it.toArtworkProto() })
    }.build()
}

fun Throwable.toFindAllArtworksFailureResponseProto(): FindAllArtworksResponseProto {
    return FindAllArtworksResponseProto.newBuilder().also { builder ->
        builder.failureBuilder.message = message.orEmpty()
    }.build()
}

fun ArtworkFullResponse.toFindArtworkFullByIdSuccessResponseProto(): FindArtworkFullByIdResponseProto {
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

fun List<ArtworkFullResponse>.toFindAllArtworksFullSuccessResponseProto(): FindAllArtworksFullResponseProto {
    return FindAllArtworksFullResponseProto.newBuilder().also { builder ->
        builder.successBuilder.addAllArtworks(this.map { it.toArtworkFullProto() })
    }.build()
}

fun Throwable.toFindAllArtworksFullFailureResponseProto(): FindAllArtworksFullResponseProto {
    return FindAllArtworksFullResponseProto.newBuilder().also { builder ->
        builder.failureBuilder.message = message.orEmpty()
    }.build()
}

fun ArtworkFullResponse.toArtworkFullProto(): ArtworkFullProto {
    return ArtworkFullProto.newBuilder().also {
        it.id = id
        it.title = title
        it.description = description
        it.width = width
        it.height = height
        it.style = style.toArtworkStyleProto()
        it.status = status.toArtworkStatusProto()
        it.artist = artist.toUserProto()
    }
        .build()
}

fun ArtworkResponse.toArtworkProto(): ArtworkProto {
    return ArtworkProto.newBuilder().also {
        it.id = id
        it.title = title
        it.description = description
        it.width = width
        it.height = height
        it.style = style.toArtworkStyleProto()
        it.status = status.toArtworkStatusProto()
        it.artistId = artistId
    }
        .build()
}

fun ArtworkStyleProto.toArtworkStyle(): ArtworkStyle {
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

fun ArtworkStyle.toArtworkStyleProto(): ArtworkStyleProto {
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

fun ArtworkStatus.toArtworkStatusProto(): ArtworkStatusProto {
    return when (this) {
        ArtworkStatus.VIEW -> ArtworkStatusProto.ARTWORK_STATUS_VIEW
        ArtworkStatus.UNKNOWN -> ArtworkStatusProto.ARTWORK_STATUS_UNSPECIFIED
        ArtworkStatus.SOLD -> ArtworkStatusProto.ARTWORK_STATUS_SOLD
        ArtworkStatus.ON_AUCTION -> ArtworkStatusProto.ARTWORK_STATUS_ON_AUCTION
    }
}
