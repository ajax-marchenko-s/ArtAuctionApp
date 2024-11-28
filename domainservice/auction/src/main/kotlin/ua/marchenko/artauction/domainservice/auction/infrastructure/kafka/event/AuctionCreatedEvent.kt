package ua.marchenko.artauction.domainservice.auction.infrastructure.kafka.event

import java.time.LocalDateTime
import ua.marchenko.artauction.domainservice.auction.domain.Auction

data class AuctionCreatedEvent(
    val auction: Auction,
    val timestamp: LocalDateTime,
) {
    companion object
}
