package ua.marchenko.artauction.domainservice.auction.domain

import java.math.BigDecimal
import java.time.LocalDateTime
import ua.marchenko.artauction.domainservice.auction.domain.Auction.Bid

data class CreateAuction(
    val artworkId: String,
    val startBid: BigDecimal,
    val buyers: List<Bid> = emptyList(),
    val startedAt: LocalDateTime,
    val finishedAt: LocalDateTime,
) {
    companion object
}
