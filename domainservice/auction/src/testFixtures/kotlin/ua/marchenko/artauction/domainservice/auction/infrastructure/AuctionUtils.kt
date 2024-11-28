package ua.marchenko.artauction.domainservice.auction.infrastructure

import java.math.BigDecimal
import org.bson.types.ObjectId
import ua.marchenko.artauction.domainservice.auction.infrastructure.rest.dto.CreateAuctionRequest

fun CreateAuctionRequest.Companion.random(artworkId: String = ObjectId().toHexString()) =
    CreateAuctionRequest(
        artworkId = artworkId,
        startBid = BigDecimal(kotlin.random.Random.nextInt(10, 100)),
        startedAt = java.time.LocalDateTime.now(),
        finishedAt = java.time.LocalDateTime.now(),
    )
