package ua.marchenko.artauction.artwork.controller.dto

import ua.marchenko.artauction.artwork.enums.ArtworkStyle

data class CreateArtworkRequest(
    val title: String,
    val description: String,
    val style: ArtworkStyle,
    val width: Int,
    val height: Int,
)
