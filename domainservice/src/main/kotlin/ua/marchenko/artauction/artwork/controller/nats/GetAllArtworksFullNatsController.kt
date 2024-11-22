package ua.marchenko.artauction.artwork.controller.nats

import com.google.protobuf.Parser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.nats.handler.api.ProtoNatsMessageHandler
import ua.marchenko.artauction.artwork.mapper.toFindAllArtworksFullFailureResponseProto
import ua.marchenko.artauction.artwork.mapper.toFindAllArtworksFullSuccessResponseProto
import ua.marchenko.artauction.artwork.service.ArtworkService
import ua.marchenko.internal.NatsSubject
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksFullRequest as FindAllArtworksFullRequestProto
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksFullResponse as FindAllArtworksFullResponseProto

@Component
class GetAllArtworksFullNatsController(
    private val artworkService: ArtworkService,
) : ProtoNatsMessageHandler<FindAllArtworksFullRequestProto, FindAllArtworksFullResponseProto> {

    override val log: Logger = LoggerFactory.getLogger(GetAllArtworksFullNatsController::class.java)

    override val subject: String = NatsSubject.Artwork.FIND_ALL_FULL

    override val queue: String = ARTWORK_QUEUE_GROUP

    override val parser: Parser<FindAllArtworksFullRequestProto> = FindAllArtworksFullRequestProto.parser()

    override fun doOnUnexpectedError(
        inMsg: FindAllArtworksFullRequestProto?,
        e: Exception
    ): Mono<FindAllArtworksFullResponseProto> =
        e.toFindAllArtworksFullFailureResponseProto().toMono()

    override fun doHandle(inMsg: FindAllArtworksFullRequestProto): Mono<FindAllArtworksFullResponseProto> {
        return artworkService.getFullAll(inMsg.page, inMsg.limit)
            .collectList()
            .map { it.toFindAllArtworksFullSuccessResponseProto() }
            .onErrorResume {
                log.error("Error in FindAllArtworksFull for {}", inMsg, it)
                it.toFindAllArtworksFullFailureResponseProto().toMono()
            }
    }

    companion object {
        private const val ARTWORK_QUEUE_GROUP = "artwork"
    }
}
