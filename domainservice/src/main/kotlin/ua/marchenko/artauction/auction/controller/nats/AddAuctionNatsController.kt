package ua.marchenko.artauction.auction.controller.nats

import com.google.protobuf.Parser
import java.time.Clock
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.nats.handler.api.ProtoNatsMessageHandler
import ua.marchenko.artauction.auction.mapper.toCreateAuctionFailureResponseProto
import ua.marchenko.artauction.auction.mapper.toCreateAuctionRequest
import ua.marchenko.artauction.auction.mapper.toCreateAuctionSuccessResponseProto
import ua.marchenko.artauction.auction.service.AuctionService
import ua.marchenko.internal.NatsSubject
import ua.marchenko.internal.input.reqreply.auction.CreateAuctionResponse as CreateAuctionResponseProto
import ua.marchenko.internal.input.reqreply.auction.CreateAuctionRequest as CreateAuctionRequestProto

@Component
class AddAuctionNatsController(
    private val auctionService: AuctionService,
    private val clock: Clock,
) : ProtoNatsMessageHandler<CreateAuctionRequestProto, CreateAuctionResponseProto> {

    override val log: Logger = LoggerFactory.getLogger(AddAuctionNatsController::class.java)

    override val subject: String = NatsSubject.Auction.CREATE

    override val queue: String = AUCTION_QUEUE_GROUP

    override val parser: Parser<CreateAuctionRequestProto> = CreateAuctionRequestProto.parser()

    override fun doOnUnexpectedError(
        inMsg: CreateAuctionRequestProto?,
        e: Exception
    ): Mono<CreateAuctionResponseProto> =
        e.toCreateAuctionFailureResponseProto().toMono()

    override fun doHandle(inMsg: CreateAuctionRequestProto): Mono<CreateAuctionResponseProto> {
        return auctionService.save(inMsg.toCreateAuctionRequest(clock))
            .map { it.toCreateAuctionSuccessResponseProto(clock) }
            .onErrorResume {
                log.error("Error in CreateAuction for {}", inMsg, it)
                it.toCreateAuctionFailureResponseProto().toMono()
            }
    }

    companion object {
        private const val AUCTION_QUEUE_GROUP = "auction"
    }
}
