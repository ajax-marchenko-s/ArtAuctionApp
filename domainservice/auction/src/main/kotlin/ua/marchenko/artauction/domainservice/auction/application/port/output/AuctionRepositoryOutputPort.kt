package ua.marchenko.artauction.domainservice.auction.application.port.output

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ua.marchenko.artauction.domainservice.auction.domain.Auction
import ua.marchenko.artauction.domainservice.auction.domain.CreateAuction
import ua.marchenko.artauction.domainservice.auction.domain.projection.AuctionFull

interface AuctionRepositoryOutputPort {
    fun save(auction: CreateAuction): Mono<Auction>
    fun findById(id: String): Mono<Auction>
    fun findFullById(id: String): Mono<AuctionFull>
    fun findAll(page: Int = 0, limit: Int = 10): Flux<Auction>
    fun findFullAll(page: Int = 0, limit: Int = 10): Flux<AuctionFull>
}
