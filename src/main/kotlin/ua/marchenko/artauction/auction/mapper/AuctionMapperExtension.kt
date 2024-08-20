package ua.marchenko.artauction.auction.mapper

import java.time.LocalDateTime
import ua.marchenko.artauction.artwork.mapper.toArtworkResponse
import ua.marchenko.artauction.auction.controller.dto.CreateAuctionRequest
import ua.marchenko.artauction.artwork.model.Artwork
import ua.marchenko.artauction.auction.controller.dto.AuctionResponse
import ua.marchenko.artauction.auction.model.Auction
import ua.marchenko.artauction.user.mapper.toUserResponse
import ua.marchenko.artauction.user.model.User

fun Auction.toAuctionResponse() = AuctionResponse(
    id?.toString() ?: "unknown",
    artwork?.toArtworkResponse() ?: Artwork().toArtworkResponse(),
    bid ?: 0.0,
    buyer?.toUserResponse(),
    startedAt ?: LocalDateTime.MIN,
    finishedAt ?: LocalDateTime.MIN,
)

fun CreateAuctionRequest.toAuction(artwork: Artwork, buyer: User?) =
    Auction(null, artwork, bid, buyer, startedAt, finishedAt)
