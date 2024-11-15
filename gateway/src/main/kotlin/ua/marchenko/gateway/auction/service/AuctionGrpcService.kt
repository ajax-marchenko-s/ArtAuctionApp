package ua.marchenko.gateway.auction.service

import com.google.protobuf.Empty
import net.devh.boot.grpc.server.service.GrpcService
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import ua.marchenko.commonmodels.auction.Auction
import ua.marchenko.gateway.auction.mapper.toAuctionProtoList
import ua.marchenko.gateway.auction.mapper.toCreateAuctionRequestProtoInternal
import ua.marchenko.gateway.auction.mapper.toCreateAuctionResponseProtoGrpc
import ua.marchenko.gateway.auction.mapper.toFindAuctionByIdRequestProtoInternal
import ua.marchenko.gateway.auction.mapper.toFindAuctionByIdResponseProtoGrpc
import ua.marchenko.gateway.common.nats.NatsClient
import ua.marchenko.grpcapi.input.reqreply.auction.CreateAuctionRequest as CreateAuctionRequestProtoGrpc
import ua.marchenko.grpcapi.input.reqreply.auction.CreateAuctionResponse as CreateAuctionResponseProtoGrpc
import ua.marchenko.grpcapi.input.reqreply.auction.FindAuctionByIdRequest as FindAuctionByIdRequestProtoGrpc
import ua.marchenko.grpcapi.input.reqreply.auction.FindAuctionByIdResponse as FindAuctionByIdResponseProtoGrpc
import ua.marchenko.internal.input.reqreply.auction.CreateAuctionResponse as CreateAuctionResponseProtoInternal
import ua.marchenko.internal.input.reqreply.auction.FindAuctionByIdResponse as FindAuctionByIdResponseProtoInternal
import ua.marchenko.internal.input.reqreply.auction.FindAllAuctionsRequest as FindAllAuctionsRequestProtoInternal
import ua.marchenko.internal.input.reqreply.auction.FindAllAuctionsResponse as FindAllAuctionsResponseProtoInternal
import ua.marchenko.grpcapi.service.auction.ReactorAuctionServiceGrpc
import ua.marchenko.internal.NatsSubject

@GrpcService
class AuctionGrpcService(private val natsClient: NatsClient) : ReactorAuctionServiceGrpc.AuctionServiceImplBase() {

    override fun subscribeToAllAuctions(request: Mono<Empty>): Flux<Auction> {
        val allAuctionRequest = FindAllAuctionsRequestProtoInternal.newBuilder().also {
            it.page = 0
            it.limit = Int.MAX_VALUE
        }.build()

        val existingAuctions = natsClient.doRequest(
            subject = NatsSubject.Auction.FIND_ALL,
            payload = allAuctionRequest,
            parser = FindAllAuctionsResponseProtoInternal.parser()
        ).flatMapMany { it.toAuctionProtoList().toFlux() }

        return natsClient.subscribeToCreatedAuction().startWith(existingAuctions)
    }

    override fun findAuctionById(request: Mono<FindAuctionByIdRequestProtoGrpc>):
            Mono<FindAuctionByIdResponseProtoGrpc> {
        return request
            .map { it.toFindAuctionByIdRequestProtoInternal() }
            .flatMap {
                natsClient.doRequest(
                    subject = NatsSubject.Auction.FIND_BY_ID,
                    payload = it,
                    parser = FindAuctionByIdResponseProtoInternal.parser()
                )
            }.map { it.toFindAuctionByIdResponseProtoGrpc() }
    }

    override fun createAuction(request: Mono<CreateAuctionRequestProtoGrpc>): Mono<CreateAuctionResponseProtoGrpc> {
        return request
            .map { it.toCreateAuctionRequestProtoInternal() }
            .flatMap {
                natsClient.doRequest(
                    subject = NatsSubject.Auction.CREATE,
                    payload = it,
                    parser = CreateAuctionResponseProtoInternal.parser()
                )
            }.map { it.toCreateAuctionResponseProtoGrpc() }
    }
}
