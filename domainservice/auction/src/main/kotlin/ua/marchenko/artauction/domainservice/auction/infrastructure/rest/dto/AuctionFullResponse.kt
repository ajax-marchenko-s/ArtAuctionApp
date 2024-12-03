package ua.marchenko.artauction.domainservice.auction.infrastructure.rest.dto

import java.math.BigDecimal
import java.time.LocalDateTime
import ua.marchenko.artauction.domainservice.artwork.infrastructure.rest.dto.ArtworkFullResponse
import ua.marchenko.artauction.core.user.dto.UserResponse

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
