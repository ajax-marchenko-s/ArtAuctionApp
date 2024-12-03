package ua.marchenko.artauction.gateway.infrastructure.rest.dto

import ua.marchenko.artauction.gateway.infrastructure.rest.dto.enums.ArtworkStatus
import ua.marchenko.artauction.gateway.infrastructure.rest.dto.enums.ArtworkStyle

data class ArtworkResponse(
    val id: String,
    val title: String,
    val description: String,
    val style: ArtworkStyle,
    val width: Int,
    val height: Int,
    val status: ArtworkStatus,
    val artistId: String,
)
