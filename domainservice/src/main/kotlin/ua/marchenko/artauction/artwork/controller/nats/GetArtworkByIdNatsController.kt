package ua.marchenko.artauction.artwork.controller.nats

import com.google.protobuf.Parser
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.nats.handler.api.ProtoNatsMessageHandler
import ua.marchenko.artauction.artwork.mapper.toFindArtworkByIdFailureResponseProto
import ua.marchenko.artauction.artwork.mapper.toFindArtworkByIdSuccessResponseProto
import ua.marchenko.artauction.artwork.service.ArtworkService
import ua.marchenko.internal.NatsSubject
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkByIdRequest as FindArtworkByIdRequestProto
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkByIdResponse as FindArtworkByIdResponseProto

@Component
class GetArtworkByIdNatsController(
    private val artworkService: ArtworkService,
) : ProtoNatsMessageHandler<FindArtworkByIdRequestProto, FindArtworkByIdResponseProto> {

    override val log = LoggerFactory.getLogger(GetArtworkByIdNatsController::class.java)

    override val subject: String = NatsSubject.Artwork.FIND_BY_ID

    override val queue: String = QUEUE_GROUP_NAME

    override val parser: Parser<FindArtworkByIdRequestProto> = FindArtworkByIdRequestProto.parser()

    override fun doOnUnexpectedError(
        inMsg: FindArtworkByIdRequestProto?,
        e: Exception
    ): Mono<FindArtworkByIdResponseProto> =
        e.toFindArtworkByIdFailureResponseProto().toMono()

    override fun doHandle(inMsg: FindArtworkByIdRequestProto): Mono<FindArtworkByIdResponseProto> {
        return artworkService.getById(inMsg.id)
            .map { it.toFindArtworkByIdSuccessResponseProto() }
            .onErrorResume {
                log.error("Error in FindArtworkById for {}", inMsg, it)
                it.toFindArtworkByIdFailureResponseProto().toMono()
            }
    }

    companion object {
        private const val QUEUE_GROUP_NAME = "artwork"
    }
}
