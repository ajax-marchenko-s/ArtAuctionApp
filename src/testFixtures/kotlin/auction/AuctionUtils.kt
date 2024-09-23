package auction

import ua.marchenko.artauction.auction.controller.dto.CreateAuctionRequest
import ua.marchenko.artauction.auction.model.MongoAuction
import artwork.random
import getRandomObjectId
import java.time.LocalDateTime
import ua.marchenko.artauction.artwork.model.MongoArtwork
import ua.marchenko.artauction.common.mongodb.id.toObjectId

fun MongoAuction.Companion.random(
    id: String? = getRandomObjectId().toHexString(),
    artwork: MongoArtwork? = MongoArtwork.random(),
    startBid: Double? = 100.0,
) = MongoAuction(
    id = id?.toObjectId(),
    artworkId = artwork?.id,
    startBid = startBid,
    startedAt = LocalDateTime.now(),
    finishedAt = LocalDateTime.now(),
)

fun CreateAuctionRequest.Companion.random(artworkId: String = getRandomObjectId().toHexString()) =
    CreateAuctionRequest(
        artworkId = artworkId,
        startBid = 100.0,
        startedAt = LocalDateTime.now(),
        finishedAt = LocalDateTime.now(),
    )
