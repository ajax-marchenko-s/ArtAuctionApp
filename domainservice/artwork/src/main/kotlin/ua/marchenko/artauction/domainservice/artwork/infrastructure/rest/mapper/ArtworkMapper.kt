package ua.marchenko.artauction.domainservice.artwork.infrastructure.rest.mapper

import ua.marchenko.artauction.domainservice.user.infrastructure.rest.mapper.toResponse
import ua.marchenko.artauction.domainservice.artwork.domain.Artwork
import ua.marchenko.artauction.domainservice.artwork.domain.projection.ArtworkFull
import ua.marchenko.artauction.domainservice.artwork.infrastructure.rest.dto.ArtworkFullResponse

fun ArtworkFull.toFullResponse(): ArtworkFullResponse = ArtworkFullResponse(
    id = id,
    title = title,
    description = description,
    style = style.toResponseStyle(),
    width = width,
    height = height,
    status = status.toResponseStatus(),
    artist = artist.toResponse()
)

fun Artwork.ArtworkStatus.toResponseStatus(): ArtworkFullResponse.ArtworkStatus {
    return when (this) {
        Artwork.ArtworkStatus.SOLD -> ArtworkFullResponse.ArtworkStatus.SOLD
        Artwork.ArtworkStatus.ON_AUCTION -> ArtworkFullResponse.ArtworkStatus.ON_AUCTION
        Artwork.ArtworkStatus.VIEW -> ArtworkFullResponse.ArtworkStatus.VIEW
        Artwork.ArtworkStatus.UNKNOWN -> ArtworkFullResponse.ArtworkStatus.UNKNOWN
    }
}

fun Artwork.ArtworkStyle.toResponseStyle(): ArtworkFullResponse.ArtworkStyle {
    return when (this) {
        Artwork.ArtworkStyle.REALISM -> ArtworkFullResponse.ArtworkStyle.REALISM
        Artwork.ArtworkStyle.IMPRESSIONISM -> ArtworkFullResponse.ArtworkStyle.IMPRESSIONISM
        Artwork.ArtworkStyle.EXPRESSIONISM -> ArtworkFullResponse.ArtworkStyle.EXPRESSIONISM
        Artwork.ArtworkStyle.CUBISM -> ArtworkFullResponse.ArtworkStyle.CUBISM
        Artwork.ArtworkStyle.SURREALISM -> ArtworkFullResponse.ArtworkStyle.SURREALISM
        Artwork.ArtworkStyle.ABSTRACT -> ArtworkFullResponse.ArtworkStyle.ABSTRACT
        Artwork.ArtworkStyle.POP_ART -> ArtworkFullResponse.ArtworkStyle.POP_ART
        Artwork.ArtworkStyle.MINIMALISM -> ArtworkFullResponse.ArtworkStyle.MINIMALISM
        Artwork.ArtworkStyle.RENAISSANCE -> ArtworkFullResponse.ArtworkStyle.RENAISSANCE
        Artwork.ArtworkStyle.UNKNOWN -> ArtworkFullResponse.ArtworkStyle.UNKNOWN
    }
}
