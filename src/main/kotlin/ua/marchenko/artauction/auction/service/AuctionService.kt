package ua.marchenko.artauction.auction.service

import ua.marchenko.artauction.auction.controller.dto.CreateAuctionRequest
import ua.marchenko.artauction.auction.controller.dto.AuctionResponse
import ua.marchenko.artauction.auction.model.Auction

interface AuctionService {
    fun getAll(): List<Auction>
    fun getById(id: String): Auction
    fun save(auction: CreateAuctionRequest): AuctionResponse
}
