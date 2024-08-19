package ua.marchenko.artauction.auction.controller.dto

import java.time.LocalDateTime

data class AuctionRequest(
    val artworkId: String,
    val bid: Double,
    val buyerId: String?,
    val startedAt: LocalDateTime,
    val finishedAt: LocalDateTime
)
