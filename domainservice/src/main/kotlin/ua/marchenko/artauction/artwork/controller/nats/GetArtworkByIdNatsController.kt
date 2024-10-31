package ua.marchenko.artauction.artwork.controller.nats

import com.google.protobuf.Parser
import io.nats.client.Connection
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import ua.marchenko.artauction.artwork.mapper.toFindArtworkByIdFailureResponseProto
import ua.marchenko.artauction.artwork.mapper.toFindArtworkByIdSuccessResponseProto
import ua.marchenko.artauction.artwork.service.ArtworkService
import ua.marchenko.artauction.common.nats.NatsController
import ua.marchenko.internal.NatsSubject
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkByIdRequest as FindArtworkByIdRequestProto
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkByIdResponse as FindArtworkByIdResponseProto

@Controller
class GetArtworkByIdNatsController(
    private val artworkService: ArtworkService,
    override val connection: Connection,
) : NatsController<FindArtworkByIdRequestProto, FindArtworkByIdResponseProto> {

    override val subject: String = NatsSubject.ArtworkNatsSubject.FIND_BY_ID

    override val queueGroup: String = QUEUE_GROUP

    override val parser: Parser<FindArtworkByIdRequestProto> = FindArtworkByIdRequestProto.parser()

    override fun errorResponse(throwable: Throwable): FindArtworkByIdResponseProto =
        throwable.toFindArtworkByIdFailureResponseProto()

    override fun handle(request: FindArtworkByIdRequestProto): Mono<FindArtworkByIdResponseProto> {
        return artworkService.getById(request.id)
            .map { it.toFindArtworkByIdSuccessResponseProto() }
            .onErrorResume {
                log.error("Error in FindArtworkById", it)
                it.toFindArtworkByIdFailureResponseProto().toMono()
            }
    }

    companion object {
        private const val QUEUE_GROUP = "artwork"
        private val log = LoggerFactory.getLogger(GetArtworkByIdNatsController::class.java)
    }
}
