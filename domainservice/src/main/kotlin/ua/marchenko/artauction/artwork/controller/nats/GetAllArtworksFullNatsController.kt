package ua.marchenko.artauction.artwork.controller.nats

import com.google.protobuf.Parser
import io.nats.client.Connection
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import ua.marchenko.artauction.artwork.mapper.toFindAllArtworksFullFailureResponseProto
import ua.marchenko.artauction.artwork.mapper.toFindAllArtworksFullSuccessResponseProto
import ua.marchenko.artauction.artwork.mapper.toFullResponse
import ua.marchenko.artauction.artwork.service.ArtworkService
import ua.marchenko.artauction.common.nats.NatsController
import ua.marchenko.internal.NatsSubject
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksFullRequest as FindAllArtworksFullRequestProto
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksFullResponse as FindAllArtworksFullResponseProto

@Controller
class GetAllArtworksFullNatsController(
    private val artworkService: ArtworkService,
    override val connection: Connection,
) : NatsController<FindAllArtworksFullRequestProto, FindAllArtworksFullResponseProto> {

    override val subject: String = NatsSubject.ArtworkNatsSubject.FIND_ALL_FULL

    override val queueGroup: String = QUEUE_GROUP

    override val parser: Parser<FindAllArtworksFullRequestProto> = FindAllArtworksFullRequestProto.parser()

    override val responseType: FindAllArtworksFullResponseProto = FindAllArtworksFullResponseProto.getDefaultInstance()

    override fun handle(request: FindAllArtworksFullRequestProto): Mono<FindAllArtworksFullResponseProto> {
        return artworkService.getFullAll(request.page, request.limit)
            .map { it.toFullResponse() }
            .collectList()
            .map { it.toFindAllArtworksFullSuccessResponseProto() }
            .onErrorResume {
                log.error("Error in FindAllArtworksFull", it)
                it.toFindAllArtworksFullFailureResponseProto().toMono()
            }
    }

    companion object {
        private const val QUEUE_GROUP = "artwork"
        private val log = LoggerFactory.getLogger(GetAllArtworksFullNatsController::class.java)
    }
}
