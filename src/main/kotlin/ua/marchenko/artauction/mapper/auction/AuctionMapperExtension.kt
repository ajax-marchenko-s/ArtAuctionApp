package ua.marchenko.artauction.mapper.auction

import ua.marchenko.artauction.dto.auction.AuctionRequest
import ua.marchenko.artauction.dto.auction.AuctionResponse
import ua.marchenko.artauction.mapper.artwork.toArtworkResponse
import ua.marchenko.artauction.mapper.user.toUserResponse
import ua.marchenko.artauction.model.Artwork
import ua.marchenko.artauction.model.Auction


fun Auction.toAuctionResponse() = AuctionResponse(id, artwork.toArtworkResponse(), bid, buyer?.toUserResponse(), endDate)

fun AuctionRequest.toAuction(artwork: Artwork) = Auction(id, artwork, bid, null, endDate)