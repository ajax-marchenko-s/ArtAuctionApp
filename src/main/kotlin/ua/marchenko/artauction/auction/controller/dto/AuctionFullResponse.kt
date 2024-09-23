package ua.marchenko.artauction.auction.controller.dto

import java.time.LocalDateTime
import ua.marchenko.artauction.artwork.controller.dto.ArtworkFullResponse
import ua.marchenko.artauction.user.controller.dto.UserResponse

data class AuctionFullResponse(
    val id: String,
    val artwork: ArtworkFullResponse,
    val startBid: Double,
    val buyers: List<BidFullResponse>,
    val startedAt: LocalDateTime,
    val finishedAt: LocalDateTime,
) {
    data class BidFullResponse(
        val buyer: UserResponse,
        val bid: Double,
    )
}
