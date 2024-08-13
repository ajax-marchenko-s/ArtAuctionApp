package ua.marchenko.artauction.dto.artwork

import ua.marchenko.artauction.enums.artwork.ArtworkStatus
import ua.marchenko.artauction.enums.artwork.ArtworkStyle

data class ArtworkRequest (
    val id: String,
    val title: String,
    val description: String,
    val style: ArtworkStyle,
    val width: Int,
    val height: Int,
    val status: ArtworkStatus,
    val artistId: String
)