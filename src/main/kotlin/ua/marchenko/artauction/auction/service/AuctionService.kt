package ua.marchenko.artauction.auction.service

import ua.marchenko.artauction.auction.controller.dto.CreateAuctionRequest
import ua.marchenko.artauction.auction.controller.dto.AuctionResponse
import ua.marchenko.artauction.auction.model.MongoAuction
import ua.marchenko.artauction.auction.model.projection.AuctionFull

interface AuctionService {
    fun getAll(): List<MongoAuction>
    fun getFullAll(): List<AuctionFull>
    fun getById(id: String): MongoAuction
    fun getFullById(id: String): AuctionFull
    fun save(auction: CreateAuctionRequest): AuctionResponse
}
