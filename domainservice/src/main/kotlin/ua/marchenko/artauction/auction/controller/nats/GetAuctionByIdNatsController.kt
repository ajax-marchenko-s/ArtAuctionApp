package ua.marchenko.artauction.auction.controller.nats

import com.google.protobuf.Parser
import io.nats.client.Connection
import java.time.Clock
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import ua.marchenko.artauction.auction.mapper.toFindAuctionByIdFailureResponseProto
import ua.marchenko.artauction.auction.mapper.toFindAuctionByIdSuccessResponseProto
import ua.marchenko.artauction.auction.service.AuctionService
import ua.marchenko.artauction.common.nats.NatsController
import ua.marchenko.internal.NatsSubject
import ua.marchenko.internal.input.reqreply.auction.FindAuctionByIdRequest as FindAuctionByIdRequestProto
import ua.marchenko.internal.input.reqreply.auction.FindAuctionByIdResponse as FindAuctionByIdResponseProto

@Component
class GetAuctionByIdNatsController(
    private val auctionService: AuctionService,
    override val connection: Connection,
    private val clock: Clock,
) : NatsController<FindAuctionByIdRequestProto, FindAuctionByIdResponseProto> {

    override val subject: String = NatsSubject.Auction.FIND_BY_ID

    override val queueGroup: String = QUEUE_GROUP

    override val parser: Parser<FindAuctionByIdRequestProto> = FindAuctionByIdRequestProto.parser()

    override fun errorResponse(throwable: Throwable): FindAuctionByIdResponseProto =
        throwable.toFindAuctionByIdFailureResponseProto()

    override fun handle(request: FindAuctionByIdRequestProto): Mono<FindAuctionByIdResponseProto> {
        return auctionService.getById(request.id)
            .map { it.toFindAuctionByIdSuccessResponseProto(clock) }
            .onErrorResume {
                log.error("Error in FindArtworkById for {}", request, it)
                it.toFindAuctionByIdFailureResponseProto().toMono()
            }
    }

    companion object {
        private const val QUEUE_GROUP = "auction"
        private val log = LoggerFactory.getLogger(GetAuctionByIdNatsController::class.java)
    }
}

