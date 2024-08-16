package ua.marchenko.artauction.auction.controller.dto

import ua.marchenko.artauction.artwork.controller.dto.ArtworkResponse
import ua.marchenko.artauction.user.controller.dto.UserResponse
import java.time.LocalDateTime

data class AuctionResponse(
    val id: String,
    val artwork: ArtworkResponse,
    val bid: Double,
    val buyer: UserResponse?,
    val startedAt: LocalDateTime,
    val finishedAt: LocalDateTime
)