package ua.marchenko.core.artwork.dto

import ua.marchenko.core.artwork.enums.ArtworkStatus
import ua.marchenko.core.artwork.enums.ArtworkStyle
import ua.marchenko.core.user.dto.UserResponse

data class ArtworkFullResponse(
    val id: String,
    val title: String,
    val description: String,
    val style: ArtworkStyle,
    val width: Int,
    val height: Int,
    val status: ArtworkStatus,
    val artist: UserResponse,
)
