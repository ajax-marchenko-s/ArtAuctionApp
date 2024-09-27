package ua.marchenko.artauction.auction.controller.dto

import java.math.BigDecimal
import java.time.LocalDateTime
import ua.marchenko.artauction.artwork.controller.dto.ArtworkFullResponse
import ua.marchenko.artauction.user.controller.dto.UserResponse

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
