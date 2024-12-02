package ua.marchenko.artauction.domainservice.artwork.domain

import ua.marchenko.artauction.domainservice.user.domain.User
import ua.marchenko.artauction.domainservice.artwork.domain.projection.ArtworkFull

fun Artwork.toFullArtwork(artist: User) = ArtworkFull(
    id = id,
    title = title,
    description = description,
    style = style,
    width = width,
    height = height,
    status = status,
    artist = artist,
)
