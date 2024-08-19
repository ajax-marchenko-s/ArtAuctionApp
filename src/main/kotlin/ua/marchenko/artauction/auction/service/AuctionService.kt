package ua.marchenko.artauction.auction.service

import ua.marchenko.artauction.auction.controller.dto.CreateAuctionRequest
import ua.marchenko.artauction.auction.controller.dto.AuctionResponse
import ua.marchenko.artauction.auction.model.Auction

interface AuctionService {
    fun findAll(): List<Auction>
    fun findById(id: String): Auction
    fun save(auction: CreateAuctionRequest): AuctionResponse
}
