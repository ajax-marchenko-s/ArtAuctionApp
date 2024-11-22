package ua.marchenko.artauction.auction.controller.nats

import com.google.protobuf.Parser
import java.time.Clock
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.nats.handler.api.ProtoNatsMessageHandler
import ua.marchenko.artauction.auction.mapper.toFindAllAuctionsFailureResponseProto
import ua.marchenko.artauction.auction.mapper.toFindAllAuctionsSuccessResponseProto
import ua.marchenko.artauction.auction.service.AuctionService
import ua.marchenko.internal.NatsSubject
import ua.marchenko.internal.input.reqreply.auction.FindAllAuctionsRequest as FindAllAuctionsRequestProto
import ua.marchenko.internal.input.reqreply.auction.FindAllAuctionsResponse as FindAllAuctionsResponseProto

@Component
class GetAllAuctionsNatsController(
    private val auctionService: AuctionService,
    private val clock: Clock,
) : ProtoNatsMessageHandler<FindAllAuctionsRequestProto, FindAllAuctionsResponseProto> {

    override val log: Logger = LoggerFactory.getLogger(GetAllAuctionsNatsController::class.java)

    override val subject: String = NatsSubject.Auction.FIND_ALL

    override val queue: String = AUCTION_QUEUE_GROUP

    override val parser: Parser<FindAllAuctionsRequestProto> = FindAllAuctionsRequestProto.parser()

    override fun doOnUnexpectedError(
        inMsg: FindAllAuctionsRequestProto?,
        e: Exception
    ): Mono<FindAllAuctionsResponseProto> =
        e.toFindAllAuctionsFailureResponseProto().toMono()

    override fun doHandle(inMsg: FindAllAuctionsRequestProto): Mono<FindAllAuctionsResponseProto> {
        return auctionService.getAll(inMsg.page, inMsg.limit)
            .collectList()
            .map { it.toFindAllAuctionsSuccessResponseProto(clock) }
            .onErrorResume {
                log.error("Error in FindAllAuctions for {}", inMsg, it)
                it.toFindAllAuctionsFailureResponseProto().toMono()
            }
    }

    companion object {
        private const val AUCTION_QUEUE_GROUP = "auction"
    }
}
