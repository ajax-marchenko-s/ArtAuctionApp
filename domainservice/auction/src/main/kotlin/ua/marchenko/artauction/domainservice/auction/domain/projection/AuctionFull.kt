package ua.marchenko.artauction.domainservice.auction.domain.projection

import java.math.BigDecimal
import java.time.LocalDateTime
import ua.marchenko.artauction.domainservice.user.domain.User
import ua.marchenko.artauction.domainservice.artwork.domain.projection.ArtworkFull

data class AuctionFull(
    val id: String,
    val artwork: ArtworkFull,
    val startBid: BigDecimal,
    val buyers: List<BidFull> = emptyList(),
    val startedAt: LocalDateTime,
    val finishedAt: LocalDateTime,
) {
    data class BidFull(
        val buyer: User,
        val bid: BigDecimal,
    ) {
        companion object
    }

    companion object
}
