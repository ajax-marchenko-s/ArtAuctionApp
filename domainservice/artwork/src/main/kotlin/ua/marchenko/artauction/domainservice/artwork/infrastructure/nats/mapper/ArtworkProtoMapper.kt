@file:Suppress("TooManyFunctions")

package ua.marchenko.artauction.domainservice.artwork.infrastructure.nats.mapper

import ua.marchenko.artauction.domainservice.user.infrastructure.proto.toUserProto
import ua.marchenko.artauction.core.artwork.exception.ArtworkNotFoundException
import ua.marchenko.artauction.core.user.exception.UserNotFoundException
import ua.marchenko.artauction.domainservice.artwork.domain.Artwork
import ua.marchenko.artauction.domainservice.artwork.domain.Artwork.ArtworkStatus
import ua.marchenko.artauction.domainservice.artwork.domain.Artwork.ArtworkStyle
import ua.marchenko.artauction.domainservice.artwork.domain.CreateArtwork
import ua.marchenko.artauction.domainservice.artwork.domain.projection.ArtworkFull
import ua.marchenko.internal.commonmodels.artwork.Artwork as ArtworkProto
import ua.marchenko.internal.commonmodels.artwork.ArtworkFull as ArtworkFullProto
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksFullResponse as FindAllArtworksFullResponseProto
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkFullByIdResponse as FindArtworkFullByIdResponseProto
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksResponse as FindAllArtworksResponseProto
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkByIdResponse as FindArtworkByIdResponseProto
import ua.marchenko.internal.input.reqreply.artwork.CreateArtworkRequest as CreateArtworkRequestProto
import ua.marchenko.internal.input.reqreply.artwork.CreateArtworkResponse as CreateArtworkResponseProto
import ua.marchenko.internal.commonmodels.artwork.Artwork.ArtworkStyle as ArtworkStyleProto
import ua.marchenko.internal.commonmodels.artwork.Artwork.ArtworkStatus as ArtworkStatusProto

fun CreateArtworkRequestProto.toDomainCreate(): CreateArtwork {
    return CreateArtwork(
        title = title,
        description = description,
        style = style.toArtworkStyle(),
        width = width,
        height = height,
        artistId = artistId,
    )
}

fun Artwork.toCreateArtworkSuccessResponseProto(): CreateArtworkResponseProto {
    return ua.marchenko.internal.input.reqreply.artwork.CreateArtworkResponse.newBuilder().also { builder ->
        builder.successBuilder.setArtwork(toArtworkProto())
    }.build()
}

fun Throwable.toCreateArtworkFailureResponseProto(): CreateArtworkResponseProto {
    return CreateArtworkResponseProto.newBuilder().also { builder ->
        builder.failureBuilder.message = message.orEmpty()
        if (this is UserNotFoundException) {
            builder.failureBuilder.userNotFoundBuilder
        }
    }.build()
}

fun Artwork.toFindArtworkByIdSuccessResponseProto(): FindArtworkByIdResponseProto {
    return ua.marchenko.internal.input.reqreply.artwork.FindArtworkByIdResponse.newBuilder().also { builder ->
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

fun List<Artwork>.toFindAllArtworksSuccessResponseProto(): FindAllArtworksResponseProto {
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
        it.id = id
        it.title = title
        it.description = description
        it.width = width
        it.height = height
        it.style = style.toArtworkStyleProto()
        it.status = status.toArtworkStatusProto()
        it.artist = artist.toUserProto()
    }.build()
}

fun Artwork.toArtworkProto(): ArtworkProto {
    return ua.marchenko.internal.commonmodels.artwork.Artwork.newBuilder().also {
        it.id = requireNotNull(id) { "artwork id cannot be null" }
        it.title = title
        it.description = description
        it.width = width
        it.height = height
        it.style = style.toArtworkStyleProto()
        it.status = status.toArtworkStatusProto()
        it.artistId = artistId
    }.build()
}

fun ArtworkStyleProto.toArtworkStyle(): ArtworkStyle {
    return when (this) {
        ArtworkStyleProto.UNRECOGNIZED -> ArtworkStyle.UNKNOWN
        ArtworkStyleProto.ARTWORK_STYLE_REALISM -> ArtworkStyle.REALISM
        ArtworkStyleProto.ARTWORK_STYLE_IMPRESSIONISM -> ArtworkStyle.IMPRESSIONISM
        ArtworkStyleProto.ARTWORK_STYLE_EXPRESSIONISM -> ArtworkStyle.EXPRESSIONISM
        ArtworkStyleProto.ARTWORK_STYLE_CUBISM -> ArtworkStyle.CUBISM
        ArtworkStyleProto.ARTWORK_STYLE_SURREALISM -> ArtworkStyle.SURREALISM
        ArtworkStyleProto.ARTWORK_STYLE_ABSTRACT -> ArtworkStyle.ABSTRACT
        ArtworkStyleProto.ARTWORK_STYLE_POP_ART -> ArtworkStyle.POP_ART
        ArtworkStyleProto.ARTWORK_STYLE_MINIMALISM -> ArtworkStyle.MINIMALISM
        ArtworkStyleProto.ARTWORK_STYLE_RENAISSANCE -> ArtworkStyle.RENAISSANCE
        ArtworkStyleProto.ARTWORK_STYLE_UNSPECIFIED ->
            throw IllegalArgumentException("Artwork style must be specified.")
    }
}

fun ArtworkStyle.toArtworkStyleProto(): ArtworkStyleProto {
    return when (this) {
        ArtworkStyle.UNKNOWN -> ArtworkStyleProto.UNRECOGNIZED
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
        ArtworkStatus.UNKNOWN -> ArtworkStatusProto.UNRECOGNIZED
        ArtworkStatus.VIEW -> ArtworkStatusProto.ARTWORK_STATUS_VIEW
        ArtworkStatus.SOLD -> ArtworkStatusProto.ARTWORK_STATUS_SOLD
        ArtworkStatus.ON_AUCTION -> ArtworkStatusProto.ARTWORK_STATUS_ON_AUCTION
    }
}
