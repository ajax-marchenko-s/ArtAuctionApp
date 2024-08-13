package ua.marchenko.artauction.model

import ua.marchenko.artauction.enums.artwork.ArtworkStatus
import ua.marchenko.artauction.enums.artwork.ArtworkStyle
import java.util.*

data class Artwork(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val style: ArtworkStyle,
    val width: Int,
    val height: Int,
    val status: ArtworkStatus,
    val artist: User
)
