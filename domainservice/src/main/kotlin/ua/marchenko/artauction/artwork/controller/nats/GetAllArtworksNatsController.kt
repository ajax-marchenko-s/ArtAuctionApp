package ua.marchenko.artauction.artwork.controller.nats

import com.google.protobuf.Parser
import io.nats.client.Connection
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import ua.marchenko.artauction.artwork.mapper.toFindAllArtworksFailureResponseProto
import ua.marchenko.artauction.artwork.mapper.toFindAllArtworksSuccessResponseProto
import ua.marchenko.artauction.artwork.service.ArtworkService
import ua.marchenko.artauction.common.nats.NatsController
import ua.marchenko.internal.NatsSubject
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksRequest as FindAllArtworksRequestProto
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksResponse as FindAllArtworksResponseProto

@Controller
class GetAllArtworksNatsController(
    private val artworkService: ArtworkService,
    override val connection: Connection,
) : NatsController<FindAllArtworksRequestProto, FindAllArtworksResponseProto> {

    override val subject: String = NatsSubject.ArtworkNatsSubject.FIND_ALL

    override val queueGroup: String = QUEUE_GROUP

    override val parser: Parser<FindAllArtworksRequestProto> = FindAllArtworksRequestProto.parser()

    override fun errorResponse(throwable: Throwable): FindAllArtworksResponseProto =
        throwable.toFindAllArtworksFailureResponseProto()

    override fun handle(request: FindAllArtworksRequestProto): Mono<FindAllArtworksResponseProto> {
        return artworkService.getAll(request.page, request.limit)
            .collectList()
            .map { it.toFindAllArtworksSuccessResponseProto() }
            .onErrorResume {
                log.error("Error in FindAllArtworks", it)
                it.toFindAllArtworksFailureResponseProto().toMono()
            }
    }

    companion object {
        private const val QUEUE_GROUP = "artwork"
        private val log = LoggerFactory.getLogger(GetAllArtworksNatsController::class.java)
    }
}
