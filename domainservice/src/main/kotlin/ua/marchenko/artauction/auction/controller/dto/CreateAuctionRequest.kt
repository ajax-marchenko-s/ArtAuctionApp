package ua.marchenko.artauction.auction.controller.dto

import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.LocalDateTime

data class CreateAuctionRequest(

    @field:NotBlank(message = "Artwork id cannot be blank")
    val artworkId: String,

    @field:Min(value = 1, message = "Auction start bid must be greater than zero")
    val startBid: BigDecimal,

    @field:NotNull(message = "Auction start time cannot be null")
    @field:FutureOrPresent(message = "Auction start time must be in the future or present")
    val startedAt: LocalDateTime,

    @field:NotNull(message = "Auction finish time cannot be null")
    @field:FutureOrPresent(message = "Auction finish time must be in the future or present")
    val finishedAt: LocalDateTime,

    ) {
    companion object
}
