package ua.marchenko.artauction.mapper.artwork

import ua.marchenko.artauction.dto.artwork.ArtworkRequest
import ua.marchenko.artauction.dto.artwork.ArtworkResponse
import ua.marchenko.artauction.mapper.user.toUser
import ua.marchenko.artauction.mapper.user.toUserResponse
import ua.marchenko.artauction.model.Artwork
import ua.marchenko.artauction.model.User

fun Artwork.toArtworkResponse() = ArtworkResponse(id, title, description, style, width, height, status, artist.toUserResponse())

fun ArtworkRequest.toArtwork(user: User) = Artwork(id, title, description, style, width, height, status, user)

fun ArtworkResponse.toArtwork() = Artwork(id, title, description, style, width, height, status, artist.toUser())