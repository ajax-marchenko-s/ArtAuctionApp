package ua.marchenko.artauction.dto.auction

import ua.marchenko.artauction.dto.artwork.ArtworkResponse
import ua.marchenko.artauction.dto.user.UserResponse
import java.time.LocalDateTime

data class AuctionResponse (
    val id: String,
    val artwork: ArtworkResponse,
    val bid: Double,
    val buyer: UserResponse?,
    val endDate: LocalDateTime
)