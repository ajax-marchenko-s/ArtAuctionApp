package ua.marchenko.artauction.domainservice.auction.application.port.output

import reactor.core.publisher.Mono
import ua.marchenko.artauction.domainservice.auction.domain.Auction

interface AuctionCreatedEventProducerOutputPort {
    fun sendCreateAuctionEvent(auction: Auction): Mono<Unit>
}
