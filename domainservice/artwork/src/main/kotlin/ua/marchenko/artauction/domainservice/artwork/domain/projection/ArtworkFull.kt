package ua.marchenko.artauction.domainservice.artwork.domain.projection

import ua.marchenko.artauction.domainservice.user.domain.User
import ua.marchenko.artauction.domainservice.artwork.domain.Artwork

data class ArtworkFull(
    val id: String,
    val title: String,
    val description: String,
    val style: Artwork.ArtworkStyle,
    val width: Int,
    val height: Int,
    val status: Artwork.ArtworkStatus,
    val artist: User,
) {
    companion object
}
