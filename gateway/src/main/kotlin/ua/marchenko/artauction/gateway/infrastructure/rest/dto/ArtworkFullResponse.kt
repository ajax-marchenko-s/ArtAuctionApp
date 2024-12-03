package ua.marchenko.artauction.gateway.infrastructure.rest.dto

import ua.marchenko.artauction.gateway.infrastructure.rest.dto.enums.ArtworkStatus
import ua.marchenko.artauction.gateway.infrastructure.rest.dto.enums.ArtworkStyle
import ua.marchenko.artauction.core.user.dto.UserResponse

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
