package ua.marchenko.artauction.domainservice.auction.application.port.input

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ua.marchenko.artauction.domainservice.auction.domain.Auction
import ua.marchenko.artauction.domainservice.auction.domain.projection.AuctionFull

interface AuctionServiceInputPort {
    fun getAll(page: Int = 0, limit: Int = 10): Flux<Auction>
    fun getFullAll(page: Int = 0, limit: Int = 10): Flux<AuctionFull>
    fun getById(id: String): Mono<Auction>
    fun getFullById(id: String): Mono<AuctionFull>
    fun save(auction: Auction): Mono<Auction>
}
