package ua.marchenko.artauction.auction.controller.dto

import java.math.BigDecimal
import java.time.LocalDateTime
import ua.marchenko.core.artwork.dto.ArtworkFullResponse
import ua.marchenko.core.user.dto.UserResponse

data class AuctionFullResponse(
    val id: String,
    val artwork: ArtworkFullResponse,
    val startBid: BigDecimal,
    val buyers: List<BidFullResponse>,
    val startedAt: LocalDateTime,
    val finishedAt: LocalDateTime,
) {
    data class BidFullResponse(
        val buyer: UserResponse,
        val bid: BigDecimal,
    )
}
