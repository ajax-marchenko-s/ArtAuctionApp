package ua.marchenko.artauction.service.auction

import ua.marchenko.artauction.dto.auction.AuctionRequest
import ua.marchenko.artauction.dto.auction.AuctionResponse

interface AuctionService {
    fun findAll(): List<AuctionResponse>
    fun findById(id: String): AuctionResponse
    fun save(auctionRequest: AuctionRequest): AuctionResponse
}