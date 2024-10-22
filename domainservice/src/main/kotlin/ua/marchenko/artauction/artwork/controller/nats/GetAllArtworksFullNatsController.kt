package ua.marchenko.artauction.artwork.controller.nats

import com.google.protobuf.Parser
import io.nats.client.Connection
import org.springframework.stereotype.Controller
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import ua.marchenko.artauction.artwork.mapper.toFindAllArtworksFullFailureResponseProto
import ua.marchenko.artauction.artwork.mapper.toFindAllArtworksFullSuccessResponseProto
import ua.marchenko.artauction.artwork.mapper.toFullResponse
import ua.marchenko.artauction.artwork.service.ArtworkService
import ua.marchenko.artauction.common.nats.NatsController
import ua.marchenko.internal.ArtworkNatsSubject
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksFullRequest as FindAllArtworksFullRequestProto
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksFullResponse as FindAllArtworksFullResponseProto

@Controller
class GetAllArtworksFullNatsController(
    private val artworkService: ArtworkService,
    override val connection: Connection,
) : NatsController<FindAllArtworksFullRequestProto, FindAllArtworksFullResponseProto> {

    override val subject: String = ArtworkNatsSubject.FIND_ALL_FULL

    override val queueGroup: String = QUEUE_GROUP

    override val parser: Parser<FindAllArtworksFullRequestProto> = FindAllArtworksFullRequestProto.parser()

    override fun handle(request: FindAllArtworksFullRequestProto): Mono<FindAllArtworksFullResponseProto> {
        return artworkService.getFullAll(request.page, request.limit)
            .map { it.toFullResponse() }
            .collectList()
            .map { it.toFindAllArtworksFullSuccessResponseProto() }
            .onErrorResume { e -> e.toFindAllArtworksFullFailureResponseProto().toMono() }
    }

    companion object {
        private const val QUEUE_GROUP = "artwork"
    }
}
