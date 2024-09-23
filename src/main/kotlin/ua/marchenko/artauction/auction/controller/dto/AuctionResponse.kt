package ua.marchenko.artauction.auction.controller.dto

import java.time.LocalDateTime

data class AuctionResponse(
    val id: String,
    val artworkId: String,
    val startBid: Double,
    val buyers: List<BidResponse>,
    val startedAt: LocalDateTime,
    val finishedAt: LocalDateTime,
) {
    data class BidResponse(
        val buyerId: String,
        val bid: Double,
    )
}
