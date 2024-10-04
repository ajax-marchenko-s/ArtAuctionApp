package ua.marchenko.artauction.auction.service

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ua.marchenko.artauction.auction.controller.dto.CreateAuctionRequest
import ua.marchenko.artauction.auction.model.MongoAuction
import ua.marchenko.artauction.auction.model.projection.AuctionFull

interface AuctionService {
    fun getAll(page: Int = 0, limit: Int = 10): Flux<MongoAuction>
    fun getFullAll(page: Int = 0, limit: Int = 10): Flux<AuctionFull>
    fun getById(id: String): Mono<MongoAuction>
    fun getFullById(id: String): Mono<AuctionFull>
    fun save(auction: CreateAuctionRequest): Mono<MongoAuction>
}
