package ua.marchenko.artauction.dto.auction

import java.time.LocalDateTime

data class AuctionRequest (
    val id: String,
    val artworkId: String,
    val bid: Double,
    val endDate: LocalDateTime
)