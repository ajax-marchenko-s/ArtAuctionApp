package auction

import ua.marchenko.artauction.artwork.model.Artwork
import ua.marchenko.artauction.auction.controller.dto.CreateAuctionRequest
import ua.marchenko.artauction.auction.model.Auction
import artwork.getRandomArtwork
import getRandomString
import java.time.LocalDateTime

fun getRandomAuction(
    id: String = getRandomString(10),
    artwork: Artwork? = getRandomArtwork(),
    bid: Double? = 100.0,
): Auction {
    return Auction(
        id = id,
        artwork = artwork,
        bid = bid,
        startedAt = LocalDateTime.now(),
        finishedAt = LocalDateTime.now(),
    )
}

fun getRandomAuctionRequest(artworkId: String = getRandomString()): CreateAuctionRequest {
    return CreateAuctionRequest(
        artworkId = artworkId,
        bid = 100.0,
        buyerId = null,
        startedAt = LocalDateTime.now(),
        finishedAt = LocalDateTime.now(),
    )
}
