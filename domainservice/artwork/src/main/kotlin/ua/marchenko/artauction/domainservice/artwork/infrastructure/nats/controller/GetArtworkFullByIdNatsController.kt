package ua.marchenko.artauction.domainservice.artwork.infrastructure.nats.controller

import com.google.protobuf.Parser
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.nats.handler.api.ProtoNatsMessageHandler
import ua.marchenko.artauction.domainservice.artwork.application.port.input.ArtworkServiceInputPort
import ua.marchenko.artauction.domainservice.artwork.infrastructure.nats.mapper.toFindArtworkFullByIdFailureResponseProto
import ua.marchenko.artauction.domainservice.artwork.infrastructure.nats.mapper.toFindArtworkFullByIdSuccessResponseProto
import ua.marchenko.internal.NatsSubject
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkFullByIdResponse
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkFullByIdRequest as FindArtworkFullByIdRequestProto
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkFullByIdResponse as FindArtworkFullByIdResponseProto

@Component
class GetArtworkFullByIdNatsController(
    private val artworkService: ArtworkServiceInputPort,
) : ProtoNatsMessageHandler<FindArtworkFullByIdRequestProto, FindArtworkFullByIdResponseProto> {

    override val log = LoggerFactory.getLogger(GetArtworkFullByIdNatsController::class.java)

    override val subject: String = NatsSubject.Artwork.FIND_BY_ID_FULL

    override val queue: String = ARTWORK_QUEUE_GROUP

    override val parser: Parser<FindArtworkFullByIdRequestProto> = FindArtworkFullByIdRequestProto.parser()

    override fun doOnUnexpectedError(
        inMsg: FindArtworkFullByIdRequestProto?,
        e: Exception
    ): Mono<FindArtworkFullByIdResponse> =
        e.toFindArtworkFullByIdFailureResponseProto().toMono()

    override fun doHandle(inMsg: FindArtworkFullByIdRequestProto): Mono<FindArtworkFullByIdResponseProto> {
        return artworkService.getFullById(inMsg.id)
            .map { it.toFindArtworkFullByIdSuccessResponseProto() }
            .onErrorResume {
                log.error("Error in FindArtworkFullById for {}", inMsg, it)
                it.toFindArtworkFullByIdFailureResponseProto().toMono()
            }
    }

    companion object {
        private const val ARTWORK_QUEUE_GROUP = "artwork"
    }
}
