package ua.marchenko.artauction.artwork.controller.nats

import com.google.protobuf.Parser
import io.nats.client.Connection
import org.springframework.stereotype.Controller
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import ua.marchenko.artauction.artwork.mapper.toCreateArtworkFailureResponseProto
import ua.marchenko.artauction.artwork.mapper.toCreateArtworkRequest
import ua.marchenko.artauction.artwork.mapper.toCreateArtworkSuccessResponseProto
import ua.marchenko.artauction.artwork.mapper.toMongo
import ua.marchenko.artauction.artwork.mapper.toResponse
import ua.marchenko.artauction.artwork.service.ArtworkService
import ua.marchenko.artauction.common.nats.NatsController
import ua.marchenko.internal.NatsSubject
import ua.marchenko.internal.input.reqreply.artwork.CreateArtworkRequest as CreateArtworkRequestProto
import ua.marchenko.internal.input.reqreply.artwork.CreateArtworkResponse as CreateArtworkResponseProto

@Controller
class AddArtworkNatsController(
    private val artworkService: ArtworkService,
    override val connection: Connection,
) : NatsController<CreateArtworkRequestProto, CreateArtworkResponseProto> {

    override val subject: String = NatsSubject.ArtworkNatsSubject.CREATE

    override val queueGroup: String = QUEUE_GROUP

    override val parser: Parser<CreateArtworkRequestProto> = CreateArtworkRequestProto.parser()

    override fun handle(request: CreateArtworkRequestProto): Mono<CreateArtworkResponseProto> {
        return artworkService.save(request.toCreateArtworkRequest().toMongo())
            .map { it.toResponse().toCreateArtworkSuccessResponseProto() }
            .onErrorResume { it.toCreateArtworkFailureResponseProto().toMono() }
    }

    companion object {
        private const val QUEUE_GROUP = "artwork"
    }
}
