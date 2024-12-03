package ua.marchenko.artauction.domainservice.auction.infrastructure.rest.dto

import java.math.BigDecimal
import java.time.LocalDateTime

data class AuctionResponse(
    val id: String,
    val artworkId: String,
    val startBid: BigDecimal,
    val buyers: List<BidResponse>,
    val startedAt: LocalDateTime,
    val finishedAt: LocalDateTime,
) {
    data class BidResponse(
        val buyerId: String,
        val bid: BigDecimal,
    )
}
