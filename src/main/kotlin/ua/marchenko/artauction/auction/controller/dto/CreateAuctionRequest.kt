package ua.marchenko.artauction.auction.controller.dto

import java.time.LocalDateTime

data class CreateAuctionRequest(
    val artworkId: String,
    val bid: Double,
    val buyerId: String?,
    val startedAt: LocalDateTime,
    val finishedAt: LocalDateTime,
) {
    companion object
}
