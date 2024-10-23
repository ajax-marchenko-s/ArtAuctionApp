package ua.marchenko.artauction.artwork.controller.nats

import com.google.protobuf.Parser
import io.nats.client.Connection
import org.springframework.stereotype.Controller
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import ua.marchenko.artauction.artwork.mapper.toFindArtworkFullByIdFailureResponseProto
import ua.marchenko.artauction.artwork.mapper.toFindArtworkFullByIdSuccessResponseProto
import ua.marchenko.artauction.artwork.mapper.toFullResponse
import ua.marchenko.artauction.artwork.service.ArtworkService
import ua.marchenko.artauction.common.nats.NatsController
import ua.marchenko.internal.NatsSubject
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkFullByIdRequest as FindArtworkFullByIdRequestProto
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkFullByIdResponse as FindArtworkFullByIdResponseProto

@Controller
class GetArtworkFullByIdNatsController(
    private val artworkService: ArtworkService,
    override val connection: Connection,
) : NatsController<FindArtworkFullByIdRequestProto, FindArtworkFullByIdResponseProto> {

    override val subject: String = NatsSubject.ArtworkNatsSubject.FIND_BY_ID_FULL

    override val queueGroup: String = QUEUE_GROUP

    override val parser: Parser<FindArtworkFullByIdRequestProto> = FindArtworkFullByIdRequestProto.parser()

    override fun handle(request: FindArtworkFullByIdRequestProto): Mono<FindArtworkFullByIdResponseProto> {
        return artworkService.getFullById(request.id)
            .map { it.toFullResponse().toFindArtworkFullByIdSuccessResponseProto() }
            .onErrorResume { e -> e.toFindArtworkFullByIdFailureResponseProto().toMono() }
    }

    companion object {
        private const val QUEUE_GROUP = "artwork"
    }
}
