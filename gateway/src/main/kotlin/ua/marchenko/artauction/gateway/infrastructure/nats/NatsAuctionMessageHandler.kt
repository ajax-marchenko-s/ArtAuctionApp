package ua.marchenko.artauction.gateway.infrastructure.nats

import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import systems.ajax.nats.handler.api.NatsHandlerManager
import systems.ajax.nats.publisher.api.NatsMessagePublisher
import ua.marchenko.artauction.gateway.application.port.input.AuctionInputPort
import ua.marchenko.commonmodels.auction.Auction
import ua.marchenko.internal.NatsSubject
import ua.marchenko.internal.input.reqreply.auction.CreateAuctionRequest
import ua.marchenko.internal.input.reqreply.auction.CreateAuctionResponse
import ua.marchenko.internal.input.reqreply.auction.FindAllAuctionsRequest
import ua.marchenko.internal.input.reqreply.auction.FindAllAuctionsResponse
import ua.marchenko.internal.input.reqreply.auction.FindAuctionByIdRequest
import ua.marchenko.internal.input.reqreply.auction.FindAuctionByIdResponse

@Component
class NatsAuctionMessageHandler(
    private val natsPublisher: NatsMessagePublisher,
    private val natsManager: NatsHandlerManager,
) : AuctionInputPort {

    override fun getAllAuctions(request: FindAllAuctionsRequest): Mono<FindAllAuctionsResponse> {
        return natsPublisher.request(
            subject = NatsSubject.Auction.FIND_ALL,
            payload = request,
            parser = FindAllAuctionsResponse.parser()
        )
    }

    override fun getAuctionById(request: FindAuctionByIdRequest): Mono<FindAuctionByIdResponse> {
        return natsPublisher.request(
            subject = NatsSubject.Auction.FIND_BY_ID,
            payload = request,
            parser = FindAuctionByIdResponse.parser()
        )
    }

    override fun createAuction(request: CreateAuctionRequest): Mono<CreateAuctionResponse> {
        return natsPublisher.request(
            subject = NatsSubject.Auction.CREATE,
            payload = request,
            parser = CreateAuctionResponse.parser()
        )
    }

    override fun subscribeToCreatedAuction(): Flux<Auction> {
        return natsManager.subscribe(NatsSubject.Auction.CREATED_EVENT) { message ->
            Auction.parseFrom(message.data)
        }
    }
}
