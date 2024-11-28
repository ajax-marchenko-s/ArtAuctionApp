package ua.marchenko.artauction.gateway.application.port.output

import reactor.core.publisher.Mono
import ua.marchenko.internal.input.reqreply.artwork.CreateArtworkRequest as CreateArtworkRequestProto
import ua.marchenko.internal.input.reqreply.artwork.CreateArtworkResponse as CreateArtworkResponseProto
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksFullRequest as FindAllArtworksFullRequestProto
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksFullResponse as FindAllArtworksFullResponseProto
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksRequest as FindAllArtworksRequestProto
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksResponse as FindAllArtworksResponseProto
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkByIdRequest as FindArtworkByIdRequestProto
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkByIdResponse as FindArtworkByIdResponseProto
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkFullByIdRequest as FindArtworkFullByIdRequestProto
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkFullByIdResponse as FindArtworkFullByIdResponseProto

interface ArtworkMessageHandlerOutputPort {
    fun getArtworkById(request: FindArtworkByIdRequestProto): Mono<FindArtworkByIdResponseProto>
    fun getFullArtworkById(request: FindArtworkFullByIdRequestProto): Mono<FindArtworkFullByIdResponseProto>
    fun getAllArtworks(request: FindAllArtworksRequestProto): Mono<FindAllArtworksResponseProto>
    fun getAllFullArtworks(request: FindAllArtworksFullRequestProto): Mono<FindAllArtworksFullResponseProto>
    fun createArtwork(request: CreateArtworkRequestProto): Mono<CreateArtworkResponseProto>
}
