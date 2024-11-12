package ua.marchenko.artauction.auction.domain

import java.time.LocalDateTime
import ua.marchenko.artauction.auction.model.MongoAuction

data class AuctionCreatedEvent(
    val auction: MongoAuction,
    val timestamp: LocalDateTime,
){
    companion object
}
