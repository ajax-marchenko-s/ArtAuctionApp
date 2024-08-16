package ua.marchenko.artauction.common.auction

import ua.marchenko.artauction.artwork.model.Artwork
import ua.marchenko.artauction.auction.controller.dto.AuctionRequest
import ua.marchenko.artauction.auction.model.Auction
import ua.marchenko.artauction.common.artwork.getRandomArtwork
import ua.marchenko.artauction.common.getRandomString
import java.time.LocalDateTime

fun getRandomAuction(id: String = getRandomString(10), artwork: Artwork? = getRandomArtwork()): Auction {
    return Auction(
        id = id,
        artwork = artwork,
        bid = 100.0,
        startedAt = LocalDateTime.now(),
        finishedAt = LocalDateTime.now()
    )
}

fun getRandomAuctionRequest(): AuctionRequest {
    return AuctionRequest(
        artworkId = getRandomString(),
        bid = 100.0,
        buyerId = null,
        startedAt = LocalDateTime.now(),
        finishedAt = LocalDateTime.now()
    )
}