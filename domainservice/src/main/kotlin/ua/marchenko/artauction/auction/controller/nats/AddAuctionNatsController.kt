package ua.marchenko.artauction.auction.controller.nats

import com.google.protobuf.Parser
import io.nats.client.Connection
import java.time.Clock
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import ua.marchenko.artauction.auction.mapper.toCreateAuctionFailureResponseProto
import ua.marchenko.artauction.auction.mapper.toCreateAuctionRequest
import ua.marchenko.artauction.auction.mapper.toCreateAuctionSuccessResponseProto
import ua.marchenko.artauction.auction.service.AuctionService
import ua.marchenko.artauction.common.nats.NatsController
import ua.marchenko.internal.NatsSubject
import ua.marchenko.internal.input.reqreply.auction.CreateAuctionResponse as CreateAuctionResponseProto
import ua.marchenko.internal.input.reqreply.auction.CreateAuctionRequest as CreateAuctionRequestProto

@Controller
class AddAuctionNatsController(
    private val auctionService: AuctionService,
    override val connection: Connection,
    private val clock: Clock,
) : NatsController<CreateAuctionRequestProto, CreateAuctionResponseProto> {

    override val subject: String = NatsSubject.AuctionNatsSubject.CREATE

    override val queueGroup: String = QUEUE_GROUP

    override val parser: Parser<CreateAuctionRequestProto> = CreateAuctionRequestProto.parser()

    override fun errorResponse(throwable: Throwable): CreateAuctionResponseProto =
        throwable.toCreateAuctionFailureResponseProto()

    override fun handle(request: CreateAuctionRequestProto): Mono<CreateAuctionResponseProto> {
        return auctionService.save(request.toCreateAuctionRequest(clock))
            .map { it.toCreateAuctionSuccessResponseProto(clock) }
            .onErrorResume {
                log.error("Error in CreateAuction", it)
                it.toCreateAuctionFailureResponseProto().toMono()
            }
    }

    companion object {
        private const val QUEUE_GROUP = "auction"
        private val log = LoggerFactory.getLogger(AddAuctionNatsController::class.java)
    }
}
