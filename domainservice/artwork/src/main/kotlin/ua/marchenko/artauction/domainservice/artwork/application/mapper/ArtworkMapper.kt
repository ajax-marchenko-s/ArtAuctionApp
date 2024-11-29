package ua.marchenko.artauction.domainservice.artwork.application.mapper

import ua.marchenko.artauction.domainservice.artwork.domain.Artwork
import ua.marchenko.artauction.domainservice.artwork.domain.Artwork.ArtworkStatus
import ua.marchenko.artauction.domainservice.artwork.domain.CreateArtwork

fun CreateArtwork.toDomain(status: ArtworkStatus): Artwork = Artwork(
    id = null,
    title = title,
    description = description,
    style = style,
    width = width,
    height = height,
    status = status,
    artistId = artistId
)
