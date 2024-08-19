package ua.marchenko.artauction.auction.mapper

import ua.marchenko.artauction.artwork.mapper.toArtworkResponse
import ua.marchenko.artauction.auction.controller.dto.AuctionRequest
import ua.marchenko.artauction.artwork.model.Artwork
import ua.marchenko.artauction.auction.controller.dto.AuctionResponse
import ua.marchenko.artauction.auction.model.Auction
import ua.marchenko.artauction.user.mapper.toUserResponse
import ua.marchenko.artauction.user.model.User


fun Auction.toAuctionResponse() = AuctionResponse(
    id ?: throwIllegalArgumentException("id"),
    artwork?.toArtworkResponse() ?: throwIllegalArgumentException("artwork"),
    bid ?: throwIllegalArgumentException("bid"),
    buyer?.toUserResponse(),
    startedAt ?: throwIllegalArgumentException("startedAt"),
    finishedAt ?: throwIllegalArgumentException("finishedAt")
)

fun AuctionRequest.toAuction(artwork: Artwork, buyer: User?) = Auction(null, artwork, bid, buyer, startedAt, finishedAt)

private fun throwIllegalArgumentException(field: String): Nothing {
    throw IllegalArgumentException("Auction entity is in an invalid state: missing required field: $field")
}
