@file:Suppress("TooManyFunctions")

package ua.marchenko.gateway.artwork.mapper

import ua.marchenko.core.artwork.exception.ArtworkNotFoundException
import ua.marchenko.gateway.user.toUserResponse
import ua.marchenko.core.artwork.enums.ArtworkStatus
import ua.marchenko.core.artwork.enums.ArtworkStyle
import ua.marchenko.core.user.exception.UserNotFoundException
import ua.marchenko.core.artwork.dto.ArtworkFullResponse
import ua.marchenko.gateway.artwork.controller.dto.ArtworkResponse
import ua.marchenko.gateway.artwork.controller.dto.CreateArtworkRequest
import ua.marchenko.internal.input.reqreply.artwork.CreateArtworkResponse
import ua.marchenko.internal.input.reqreply.artwork.CreateArtworkRequest as CreateArtworkRequestProto
import ua.marchenko.internal.commonmodels.artwork.ArtworkStyle as ArtworkStyleProto
import ua.marchenko.internal.input.reqreply.artwork.CreateArtworkResponse as CreateArtworkResponseProto
import ua.marchenko.internal.commonmodels.artwork.Artwork as ArtworkProto
import ua.marchenko.internal.commonmodels.artwork.ArtworkFull as ArtworkFullProto
import ua.marchenko.internal.commonmodels.artwork.ArtworkStatus as ArtworkStatusProto
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkByIdResponse as FindArtworkByIdResponseProto
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkFullByIdResponse as FindArtworkFullByIdResponseProto
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksFullResponse as FindAllArtworksFullResponseProto
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksResponse as FindAllArtworksResponseProto

fun CreateArtworkRequest.toCreateArtworkRequestProto(): CreateArtworkRequestProto =
    CreateArtworkRequestProto.newBuilder().also {
        it.title = title
        it.description = description
        it.width = width
        it.height = height
        it.style = style.toArtworkStyleProto()
        it.artistId = artistId
    }.build()

fun CreateArtworkResponseProto.toArtworkResponse(): ArtworkResponse {
    return when (responseCase!!) {
        CreateArtworkResponseProto.ResponseCase.SUCCESS -> success.artwork.toArtworkResponse()
        CreateArtworkResponseProto.ResponseCase.RESPONSE_NOT_SET -> error("Response not set")
        CreateArtworkResponseProto.ResponseCase.FAILURE -> {
            when (failure.errorCase!!) {
                CreateArtworkResponse.Failure.ErrorCase.USER_NOT_FOUND ->
                    throw UserNotFoundException(value = failure.message)

                CreateArtworkResponse.Failure.ErrorCase.ERROR_NOT_SET -> error(failure.message)
            }
        }
    }
}

fun FindArtworkByIdResponseProto.toArtworkResponse(): ArtworkResponse {
    return when (responseCase!!) {
        FindArtworkByIdResponseProto.ResponseCase.SUCCESS -> success.artwork.toArtworkResponse()
        FindArtworkByIdResponseProto.ResponseCase.RESPONSE_NOT_SET -> error("Response not set")
        FindArtworkByIdResponseProto.ResponseCase.FAILURE -> {
            when (failure.errorCase!!) {
                FindArtworkByIdResponseProto.Failure.ErrorCase.NOT_FOUND_BY_ID ->
                    throw ArtworkNotFoundException(failure.message)

                FindArtworkByIdResponseProto.Failure.ErrorCase.ERROR_NOT_SET ->
                    error(failure.message)
            }
        }
    }
}

fun FindAllArtworksResponseProto.toArtworksList(): List<ArtworkResponse> {
    return when (responseCase!!) {
        FindAllArtworksResponseProto.ResponseCase.SUCCESS -> success.artworksList.map { it.toArtworkResponse() }
        FindAllArtworksResponseProto.ResponseCase.RESPONSE_NOT_SET -> error("Response not set")
        FindAllArtworksResponseProto.ResponseCase.FAILURE -> error(failure.message)
    }
}

fun FindArtworkFullByIdResponseProto.toArtworkFullResponse(): ArtworkFullResponse {
    return when (responseCase!!) {
        FindArtworkFullByIdResponseProto.ResponseCase.SUCCESS -> success.artwork.toArtworkFullResponse()
        FindArtworkFullByIdResponseProto.ResponseCase.RESPONSE_NOT_SET -> error("Response not set")
        FindArtworkFullByIdResponseProto.ResponseCase.FAILURE -> {
            when (failure.errorCase!!) {
                FindArtworkFullByIdResponseProto.Failure.ErrorCase.NOT_FOUND_BY_ID ->
                    throw ArtworkNotFoundException(failure.message)

                FindArtworkFullByIdResponseProto.Failure.ErrorCase.ERROR_NOT_SET ->
                    error(failure.message)
            }
        }
    }
}

fun FindAllArtworksFullResponseProto.toFullArtworkList(): List<ArtworkFullResponse> {
    return when (responseCase!!) {
        FindAllArtworksFullResponseProto.ResponseCase.SUCCESS -> success.artworksList.map { it.toArtworkFullResponse() }
        FindAllArtworksFullResponseProto.ResponseCase.FAILURE -> error(failure.message)
        FindAllArtworksFullResponseProto.ResponseCase.RESPONSE_NOT_SET -> error("Response not set")
    }
}

fun ArtworkProto.toArtworkResponse(): ArtworkResponse {
    require(style != ArtworkStyleProto.ARTWORK_STYLE_UNSPECIFIED) { "Artwork style must be specified." }
    require(status != ArtworkStatusProto.ARTWORK_STATUS_UNSPECIFIED) { "Artwork status must be specified." }
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

fun ArtworkFullProto.toArtworkFullResponse(): ArtworkFullResponse {
    require(style != ArtworkStyleProto.ARTWORK_STYLE_UNSPECIFIED) { "Artwork style must be specified." }
    require(status != ArtworkStatusProto.ARTWORK_STATUS_UNSPECIFIED) { "Artwork status must be specified." }
    return ArtworkFullResponse(
        id = id,
        title = title,
        description = description,
        style = style.toArtworkStyle(),
        width = width,
        height = height,
        status = status.toArtworkStatus(),
        artist = artist.toUserResponse()
    )
}

fun ArtworkStyleProto.toArtworkStyle(): ArtworkStyle {
    return when (this) {
        ArtworkStyleProto.UNRECOGNIZED -> ArtworkStyle.UNKNOWN
        ArtworkStyleProto.ARTWORK_STYLE_UNSPECIFIED -> ArtworkStyle.NOT_SPECIFIED
        ArtworkStyleProto.ARTWORK_STYLE_REALISM -> ArtworkStyle.REALISM
        ArtworkStyleProto.ARTWORK_STYLE_IMPRESSIONISM -> ArtworkStyle.IMPRESSIONISM
        ArtworkStyleProto.ARTWORK_STYLE_EXPRESSIONISM -> ArtworkStyle.EXPRESSIONISM
        ArtworkStyleProto.ARTWORK_STYLE_CUBISM -> ArtworkStyle.CUBISM
        ArtworkStyleProto.ARTWORK_STYLE_SURREALISM -> ArtworkStyle.SURREALISM
        ArtworkStyleProto.ARTWORK_STYLE_ABSTRACT -> ArtworkStyle.ABSTRACT
        ArtworkStyleProto.ARTWORK_STYLE_POP_ART -> ArtworkStyle.POP_ART
        ArtworkStyleProto.ARTWORK_STYLE_MINIMALISM -> ArtworkStyle.MINIMALISM
        ArtworkStyleProto.ARTWORK_STYLE_RENAISSANCE -> ArtworkStyle.RENAISSANCE
    }
}

fun ArtworkStyle.toArtworkStyleProto(): ArtworkStyleProto {
    return when (this) {
        ArtworkStyle.UNKNOWN -> ArtworkStyleProto.UNRECOGNIZED
        ArtworkStyle.NOT_SPECIFIED -> ArtworkStyleProto.ARTWORK_STYLE_UNSPECIFIED
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

fun ArtworkStatusProto.toArtworkStatus(): ArtworkStatus {
    return when (this) {
        ArtworkStatusProto.UNRECOGNIZED -> ArtworkStatus.UNKNOWN
        ArtworkStatusProto.ARTWORK_STATUS_UNSPECIFIED -> ArtworkStatus.NOT_SPECIFIED
        ArtworkStatusProto.ARTWORK_STATUS_VIEW -> ArtworkStatus.VIEW
        ArtworkStatusProto.ARTWORK_STATUS_SOLD -> ArtworkStatus.SOLD
        ArtworkStatusProto.ARTWORK_STATUS_ON_AUCTION -> ArtworkStatus.ON_AUCTION
    }
}
