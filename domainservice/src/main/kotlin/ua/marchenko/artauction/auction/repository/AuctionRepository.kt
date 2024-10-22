package ua.marchenko.artauction.auction.repository

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ua.marchenko.artauction.auction.model.MongoAuction
import ua.marchenko.artauction.auction.model.projection.AuctionFull

interface AuctionRepository {
    fun save(auction: MongoAuction): Mono<MongoAuction>
    fun findById(id: String): Mono<MongoAuction>
    fun findFullById(id: String): Mono<AuctionFull>
    fun findAll(page: Int = 0, limit: Int = 10): Flux<MongoAuction>
    fun findFullAll(page: Int = 0, limit: Int = 10): Flux<AuctionFull>
}
