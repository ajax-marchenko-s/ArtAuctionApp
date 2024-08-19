package ua.marchenko.artauction.artwork.controller.dto

import ua.marchenko.artauction.artwork.enums.ArtworkStyle

data class ArtworkRequest(
    val title: String,
    val description: String,
    val style: ArtworkStyle,
    val width: Int,
    val height: Int
)
