package ua.marchenko.artauction.gateway.application.port.input

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ua.marchenko.commonmodels.auction.Auction
import ua.marchenko.internal.input.reqreply.auction.FindAllAuctionsRequest as FindAllAuctionsRequestProto
import ua.marchenko.internal.input.reqreply.auction.FindAllAuctionsResponse as FindAllAuctionsResponseProto
import ua.marchenko.internal.input.reqreply.auction.FindAuctionByIdRequest as FindAuctionByIdRequestProto
import ua.marchenko.internal.input.reqreply.auction.FindAuctionByIdResponse as FindAuctionByIdResponseProto
import ua.marchenko.internal.input.reqreply.auction.CreateAuctionRequest as CreateAuctionRequestProto
import ua.marchenko.internal.input.reqreply.auction.CreateAuctionResponse as CreateAuctionResponseProto

interface AuctionMessageHandlerInputPort {
    fun getAllAuctions(request: FindAllAuctionsRequestProto): Mono<FindAllAuctionsResponseProto>
    fun getAuctionById(request: FindAuctionByIdRequestProto): Mono<FindAuctionByIdResponseProto>
    fun createAuction(request: CreateAuctionRequestProto): Mono<CreateAuctionResponseProto>
    fun subscribeToCreatedAuction(): Flux<Auction>
}
