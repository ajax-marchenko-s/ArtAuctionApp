package ua.marchenko.artauction.domainservice.artwork.infrastructure.nats.controller

import com.google.protobuf.Parser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.nats.handler.api.ProtoNatsMessageHandler
import ua.marchenko.artauction.domainservice.artwork.application.port.input.ArtworkServiceInputPort
import ua.marchenko.artauction.domainservice.artwork.infrastructure.nats.mapper.toCreateArtworkFailureResponseProto
import ua.marchenko.artauction.domainservice.artwork.infrastructure.nats.mapper.toCreateArtworkSuccessResponseProto
import ua.marchenko.artauction.domainservice.artwork.infrastructure.nats.mapper.toDomainCreate
import ua.marchenko.internal.NatsSubject
import ua.marchenko.internal.input.reqreply.artwork.CreateArtworkRequest as CreateArtworkRequestProto
import ua.marchenko.internal.input.reqreply.artwork.CreateArtworkResponse as CreateArtworkResponseProto

@Component
class AddArtworkNatsController(
    private val artworkService: ArtworkServiceInputPort,
) : ProtoNatsMessageHandler<CreateArtworkRequestProto, CreateArtworkResponseProto> {

    override val log: Logger = LoggerFactory.getLogger(AddArtworkNatsController::class.java)

    override val subject: String = NatsSubject.Artwork.CREATE

    override val queue: String = ARTWORK_QUEUE_GROUP

    override val parser: Parser<CreateArtworkRequestProto> = CreateArtworkRequestProto.parser()

    override fun doOnUnexpectedError(
        inMsg: CreateArtworkRequestProto?,
        e: Exception
    ): Mono<CreateArtworkResponseProto> =
        e.toCreateArtworkFailureResponseProto().toMono()

    override fun doHandle(inMsg: CreateArtworkRequestProto): Mono<CreateArtworkResponseProto> {
        return artworkService.save(inMsg.toDomainCreate())
            .map { it.toCreateArtworkSuccessResponseProto() }
            .onErrorResume {
                log.error("Error in CreateArtwork for {}", inMsg, it)
                it.toCreateArtworkFailureResponseProto().toMono()
            }
    }

    companion object {
        private const val ARTWORK_QUEUE_GROUP = "artwork"
    }
}
