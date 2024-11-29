package ua.marchenko.artauction.gateway.infrastructure.nats

import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import systems.ajax.nats.publisher.api.NatsMessagePublisher
import ua.marchenko.artauction.gateway.application.port.input.ArtworkMessageHandlerInputPort
import ua.marchenko.internal.NatsSubject
import ua.marchenko.internal.input.reqreply.artwork.CreateArtworkRequest
import ua.marchenko.internal.input.reqreply.artwork.CreateArtworkResponse
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksFullRequest
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksFullResponse
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksRequest
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksResponse
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkByIdRequest
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkByIdResponse
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkFullByIdRequest
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkFullByIdResponse

@Component
class NatsArtworkMessageHandler(
    private val natsPublisher: NatsMessagePublisher,
) : ArtworkMessageHandlerInputPort {

    override fun getArtworkById(request: FindArtworkByIdRequest): Mono<FindArtworkByIdResponse> {
        return natsPublisher.request(
            subject = NatsSubject.Artwork.FIND_BY_ID,
            payload = request,
            parser = FindArtworkByIdResponse.parser()
        )
    }

    override fun getFullArtworkById(request: FindArtworkFullByIdRequest): Mono<FindArtworkFullByIdResponse> {
        return natsPublisher.request(
            subject = NatsSubject.Artwork.FIND_BY_ID_FULL,
            payload = request,
            parser = FindArtworkFullByIdResponse.parser()
        )
    }

    override fun getAllArtworks(request: FindAllArtworksRequest): Mono<FindAllArtworksResponse> {
        return natsPublisher.request(
            subject = NatsSubject.Artwork.FIND_ALL,
            payload = request,
            parser = FindAllArtworksResponse.parser()
        )
    }

    override fun getAllFullArtworks(request: FindAllArtworksFullRequest): Mono<FindAllArtworksFullResponse> {
        return natsPublisher.request(
            subject = NatsSubject.Artwork.FIND_ALL_FULL,
            payload = request,
            parser = FindAllArtworksFullResponse.parser()
        )
    }

    override fun createArtwork(request: CreateArtworkRequest): Mono<CreateArtworkResponse> {
        return natsPublisher.request(
            subject = NatsSubject.Artwork.CREATE,
            payload = request,
            parser = CreateArtworkResponse.parser()
        )
    }
}
