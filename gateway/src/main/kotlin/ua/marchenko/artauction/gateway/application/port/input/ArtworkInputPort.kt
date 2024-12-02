package ua.marchenko.artauction.gateway.application.port.input

import reactor.core.publisher.Mono
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

interface ArtworkInputPort {
    fun getArtworkById(request: FindArtworkByIdRequest): Mono<FindArtworkByIdResponse>
    fun getFullArtworkById(request: FindArtworkFullByIdRequest): Mono<FindArtworkFullByIdResponse>
    fun getAllArtworks(request: FindAllArtworksRequest): Mono<FindAllArtworksResponse>
    fun getAllFullArtworks(request: FindAllArtworksFullRequest): Mono<FindAllArtworksFullResponse>
    fun createArtwork(request: CreateArtworkRequest): Mono<CreateArtworkResponse>
}
