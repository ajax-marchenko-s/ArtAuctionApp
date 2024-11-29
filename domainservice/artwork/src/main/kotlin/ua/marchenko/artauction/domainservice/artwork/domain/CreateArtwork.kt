package ua.marchenko.artauction.domainservice.artwork.domain

import ua.marchenko.artauction.domainservice.artwork.domain.Artwork.ArtworkStyle

data class CreateArtwork(
    val title: String,
    val description: String,
    val style: ArtworkStyle,
    val width: Int,
    val height: Int,
    val artistId: String,
) {
    companion object
}
