package ua.marchenko.artauction.gateway.application.port.input

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ua.marchenko.commonmodels.auction.Auction
import ua.marchenko.internal.input.reqreply.auction.FindAllAuctionsRequest
import ua.marchenko.internal.input.reqreply.auction.FindAllAuctionsResponse
import ua.marchenko.internal.input.reqreply.auction.FindAuctionByIdRequest
import ua.marchenko.internal.input.reqreply.auction.FindAuctionByIdResponse
import ua.marchenko.internal.input.reqreply.auction.CreateAuctionRequest
import ua.marchenko.internal.input.reqreply.auction.CreateAuctionResponse

interface AuctionInputPort {
    fun getAllAuctions(request: FindAllAuctionsRequest): Mono<FindAllAuctionsResponse>
    fun getAuctionById(request: FindAuctionByIdRequest): Mono<FindAuctionByIdResponse>
    fun createAuction(request: CreateAuctionRequest): Mono<CreateAuctionResponse>
    fun subscribeToCreatedAuction(): Flux<Auction>
}
