package ua.marchenko.artauction.domainservice.auction.infrastructure.nats.controller

import com.google.protobuf.Parser
import java.time.Clock
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.nats.handler.api.ProtoNatsMessageHandler
import ua.marchenko.artauction.domainservice.auction.application.port.input.AuctionServiceInputPort
import ua.marchenko.artauction.domainservice.auction.infrastructure.nats.mapper.toFindAuctionByIdFailureResponseProto
import ua.marchenko.artauction.domainservice.auction.infrastructure.nats.mapper.toFindAuctionByIdSuccessResponseProto
import ua.marchenko.internal.NatsSubject
import ua.marchenko.internal.input.reqreply.auction.FindAuctionByIdRequest as FindAuctionByIdRequestProto
import ua.marchenko.internal.input.reqreply.auction.FindAuctionByIdResponse as FindAuctionByIdResponseProto

@Component
class GetAuctionByIdNatsController(
    private val auctionService: AuctionServiceInputPort,
    private val clock: Clock,
) : ProtoNatsMessageHandler<FindAuctionByIdRequestProto, FindAuctionByIdResponseProto> {

    override val log: Logger = LoggerFactory.getLogger(GetAuctionByIdNatsController::class.java)

    override val subject: String = NatsSubject.Auction.FIND_BY_ID

    override val queue: String = AUCTION_QUEUE_GROUP

    override val parser: Parser<FindAuctionByIdRequestProto> = FindAuctionByIdRequestProto.parser()

    override fun doOnUnexpectedError(
        inMsg: FindAuctionByIdRequestProto?,
        e: Exception
    ): Mono<FindAuctionByIdResponseProto> =
        e.toFindAuctionByIdFailureResponseProto().toMono()

    override fun doHandle(inMsg: FindAuctionByIdRequestProto): Mono<FindAuctionByIdResponseProto> {
        return auctionService.getById(inMsg.id)
            .map { it.toFindAuctionByIdSuccessResponseProto(clock) }
            .onErrorResume {
                log.error("Error in FindArtworkById for {}", inMsg, it)
                it.toFindAuctionByIdFailureResponseProto().toMono()
            }
    }

    companion object {
        private const val AUCTION_QUEUE_GROUP = "auction"
    }
}

