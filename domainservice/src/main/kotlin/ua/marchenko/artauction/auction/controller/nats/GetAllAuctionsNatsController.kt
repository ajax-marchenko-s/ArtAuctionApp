package ua.marchenko.artauction.auction.controller.nats

import com.google.protobuf.Parser
import io.nats.client.Connection
import java.time.Clock
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import ua.marchenko.artauction.auction.mapper.toFindAllAuctionsFailureResponseProto
import ua.marchenko.artauction.auction.mapper.toFindAllAuctionsSuccessResponseProto
import ua.marchenko.artauction.auction.service.AuctionService
import ua.marchenko.artauction.common.nats.NatsController
import ua.marchenko.internal.NatsSubject
import ua.marchenko.internal.input.reqreply.auction.FindAllAuctionsRequest as FindAllAuctionsRequestProto
import ua.marchenko.internal.input.reqreply.auction.FindAllAuctionsResponse as FindAllAuctionsResponseProto

@Controller
class GetAllAuctionsNatsController(
    private val auctionService: AuctionService,
    override val connection: Connection,
    private val clock: Clock,
) : NatsController<FindAllAuctionsRequestProto, FindAllAuctionsResponseProto> {

    override val subject: String = NatsSubject.AuctionNatsSubject.FIND_ALL

    override val queueGroup: String = QUEUE_GROUP

    override val parser: Parser<FindAllAuctionsRequestProto> = FindAllAuctionsRequestProto.parser()

    override fun errorResponse(throwable: Throwable): FindAllAuctionsResponseProto =
        throwable.toFindAllAuctionsFailureResponseProto()

    override fun handle(request: FindAllAuctionsRequestProto): Mono<FindAllAuctionsResponseProto> {
        return auctionService.getAll(request.page, request.limit)
            .collectList()
            .map { it.toFindAllAuctionsSuccessResponseProto(clock) }
            .onErrorResume {
                log.error("Error in FindAllAuctions", it)
                it.toFindAllAuctionsFailureResponseProto().toMono()
            }
    }

    companion object {
        private const val QUEUE_GROUP = "auction"
        private val log = LoggerFactory.getLogger(GetAllAuctionsNatsController::class.java)
    }
}
