package ua.marchenko.artauction.artwork.controller.nats

import com.google.protobuf.Parser
import io.nats.client.Connection
import org.springframework.stereotype.Controller
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import ua.marchenko.artauction.artwork.mapper.toFindArtworkByIdFailureResponseProto
import ua.marchenko.artauction.artwork.mapper.toFindArtworkByIdSuccessResponseProto
import ua.marchenko.artauction.artwork.mapper.toResponse
import ua.marchenko.artauction.artwork.service.ArtworkService
import ua.marchenko.artauction.common.nats.NatsController
import ua.marchenko.internal.NatsSubject
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkByIdRequest
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkByIdResponse
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

    override fun handle(request: FindArtworkByIdRequest): Mono<FindArtworkByIdResponse> {
        return artworkService.getById(request.id)
            .map { it.toResponse().toFindArtworkByIdSuccessResponseProto() }
            .onErrorResume { e -> e.toFindArtworkByIdFailureResponseProto().toMono() }
    }

    companion object {
        private const val QUEUE_GROUP = "artwork"
    }
}
