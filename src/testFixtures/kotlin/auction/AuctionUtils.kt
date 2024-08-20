package auction

import ua.marchenko.artauction.artwork.model.Artwork
import ua.marchenko.artauction.auction.controller.dto.CreateAuctionRequest
import ua.marchenko.artauction.auction.model.Auction
import artwork.getRandomArtwork
import getRandomObjectId
import getRandomString
import java.time.LocalDateTime
import ua.marchenko.artauction.common.mongodb.id.toObjectId

fun getRandomAuction(
    id: String = getRandomObjectId().toString(),
    artwork: Artwork? = getRandomArtwork(),
    bid: Double? = 100.0,
): Auction {
    return Auction(
        id = id.toObjectId(),
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
