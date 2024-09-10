package auction

import ua.marchenko.artauction.artwork.model.Artwork
import ua.marchenko.artauction.auction.controller.dto.CreateAuctionRequest
import ua.marchenko.artauction.auction.model.Auction
import artwork.random
import getRandomObjectId
import java.time.LocalDateTime
import ua.marchenko.artauction.common.mongodb.id.toObjectId

fun Auction.Companion.random(
    id: String? = getRandomObjectId().toHexString(),
    artwork: Artwork? = Artwork.random(),
    bid: Double? = 100.0,
) = Auction(
    id = id?.toObjectId(),
    artwork = artwork,
    bid = bid,
    startedAt = LocalDateTime.now(),
    finishedAt = LocalDateTime.now(),
)

fun CreateAuctionRequest.Companion.random(artworkId: String = getRandomObjectId().toHexString()) =
    CreateAuctionRequest(
        artworkId = artworkId,
        bid = 100.0,
        buyerId = null,
        startedAt = LocalDateTime.now(),
        finishedAt = LocalDateTime.now(),
    )
