package ua.marchenko.artauction.dto.artwork

import ua.marchenko.artauction.dto.user.UserResponse
import ua.marchenko.artauction.enums.artwork.ArtworkStatus
import ua.marchenko.artauction.enums.artwork.ArtworkStyle
import java.util.*

data class ArtworkResponse (
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val style: ArtworkStyle,
    val width: Int,
    val height: Int,
    val status: ArtworkStatus,
    val artist: UserResponse
)