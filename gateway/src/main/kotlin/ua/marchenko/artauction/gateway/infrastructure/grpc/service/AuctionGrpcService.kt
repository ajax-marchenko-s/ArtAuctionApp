package ua.marchenko.artauction.gateway.infrastructure.grpc.service

import com.google.protobuf.Empty
import net.devh.boot.grpc.server.service.GrpcService
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import ua.marchenko.artauction.gateway.application.port.input.AuctionInputPort
import ua.marchenko.artauction.gateway.infrastructure.grpc.mapper.toAuctionProtoList
import ua.marchenko.artauction.gateway.infrastructure.grpc.mapper.toCreateAuctionRequestProtoInternal
import ua.marchenko.artauction.gateway.infrastructure.grpc.mapper.toCreateAuctionResponseProtoGrpc
import ua.marchenko.artauction.gateway.infrastructure.grpc.mapper.toFindAuctionByIdRequestProtoInternal
import ua.marchenko.artauction.gateway.infrastructure.grpc.mapper.toFindAuctionByIdResponseProtoGrpc
import ua.marchenko.commonmodels.auction.Auction
import ua.marchenko.grpcapi.input.reqreply.auction.CreateAuctionRequest
import ua.marchenko.grpcapi.input.reqreply.auction.CreateAuctionResponse
import ua.marchenko.grpcapi.input.reqreply.auction.FindAuctionByIdRequest
import ua.marchenko.grpcapi.input.reqreply.auction.FindAuctionByIdResponse
import ua.marchenko.grpcapi.service.auction.ReactorAuctionServiceGrpc
import ua.marchenko.internal.input.reqreply.auction.FindAllAuctionsRequest

@GrpcService
class AuctionGrpcService(
    private val auctionMessageHandlerInputPort: AuctionInputPort
) : ReactorAuctionServiceGrpc.AuctionServiceImplBase() {

    override fun subscribeToAllAuctions(request: Mono<Empty>): Flux<Auction> {
        val allAuctionRequest = FindAllAuctionsRequest.newBuilder().apply {
            page = 0
            limit = Int.MAX_VALUE
        }.build()

        val existingAuctions =
            auctionMessageHandlerInputPort.getAllAuctions(allAuctionRequest)
                .flatMapMany { it.toAuctionProtoList().toFlux() }

        return auctionMessageHandlerInputPort.subscribeToCreatedAuction().startWith(existingAuctions)
    }

    override fun findAuctionById(request: Mono<FindAuctionByIdRequest>):
            Mono<FindAuctionByIdResponse> {
        return request
            .map { it.toFindAuctionByIdRequestProtoInternal() }
            .flatMap {
                auctionMessageHandlerInputPort.getAuctionById(it)
            }.map { it.toFindAuctionByIdResponseProtoGrpc() }
    }

    override fun createAuction(request: Mono<CreateAuctionRequest>): Mono<CreateAuctionResponse> {
        return request
            .map { it.toCreateAuctionRequestProtoInternal() }
            .flatMap {
                auctionMessageHandlerInputPort.createAuction(it)
            }.map { it.toCreateAuctionResponseProtoGrpc() }
    }
}
