package ua.marchenko.artauction.domainservice.auction.domain

import java.math.BigDecimal
import java.time.LocalDateTime

data class Auction(
    val id: String,
    val artworkId: String,
    val startBid: BigDecimal,
    val buyers: List<Bid>,
    val startedAt: LocalDateTime,
    val finishedAt: LocalDateTime,
) {
    data class Bid(
        val buyerId: String,
        val bid: BigDecimal,
    ) {
        companion object
    }

    companion object
}
