package ua.marchenko.artauction.artwork

import ua.marchenko.artauction.artwork.model.MongoArtwork
import ua.marchenko.artauction.artwork.model.projection.ArtworkFull
import ua.marchenko.artauction.user.model.MongoUser

fun MongoArtwork.toFullArtwork(artist: MongoUser? = null) = ArtworkFull(
    id = id,
    title = title,
    description = description,
    style = style,
    width = width,
    height = height,
    status = status,
    artist = artist,
)
