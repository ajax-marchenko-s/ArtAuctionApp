package ua.marchenko.artauction.artwork.controller.nats

import com.google.protobuf.Parser
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.nats.handler.api.ProtoNatsMessageHandler
import ua.marchenko.artauction.artwork.mapper.toFindAllArtworksFailureResponseProto
import ua.marchenko.artauction.artwork.mapper.toFindAllArtworksSuccessResponseProto
import ua.marchenko.artauction.artwork.service.ArtworkService
import ua.marchenko.internal.NatsSubject
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksRequest as FindAllArtworksRequestProto
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksResponse as FindAllArtworksResponseProto

@Component
class GetAllArtworksNatsController(
    private val artworkService: ArtworkService,
) : ProtoNatsMessageHandler<FindAllArtworksRequestProto, FindAllArtworksResponseProto> {

    override val log = LoggerFactory.getLogger(GetAllArtworksNatsController::class.java)

    override val subject: String = NatsSubject.Artwork.FIND_ALL

    override val queue: String = QUEUE_GROUP_NAME

    override val parser: Parser<FindAllArtworksRequestProto> = FindAllArtworksRequestProto.parser()

    override fun doOnUnexpectedError(
        inMsg: FindAllArtworksRequestProto?,
        e: Exception
    ): Mono<FindAllArtworksResponseProto> =
        e.toFindAllArtworksFailureResponseProto().toMono()

    override fun doHandle(inMsg: FindAllArtworksRequestProto): Mono<FindAllArtworksResponseProto> {
        return artworkService.getAll(inMsg.page, inMsg.limit)
            .collectList()
            .map { it.toFindAllArtworksSuccessResponseProto() }
            .onErrorResume {
                log.error("Error in FindAllArtworks for {}", inMsg, it)
                it.toFindAllArtworksFailureResponseProto().toMono()
            }
    }

    companion object {
        private const val QUEUE_GROUP_NAME = "artwork"
    }
}
