package ua.marchenko.artauction.domainservice.artwork.application.port.input

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ua.marchenko.artauction.domainservice.artwork.domain.Artwork
import ua.marchenko.artauction.domainservice.artwork.domain.CreateArtwork
import ua.marchenko.artauction.domainservice.artwork.domain.projection.ArtworkFull

interface ArtworkServiceInputPort {
    fun getAll(page: Int = 0, limit: Int = 10): Flux<Artwork>
    fun getFullAll(page: Int = 0, limit: Int = 10): Flux<ArtworkFull>
    fun getById(id: String): Mono<Artwork>
    fun getFullById(id: String): Mono<ArtworkFull>
    fun save(artwork: CreateArtwork): Mono<Artwork>
    fun update(artworkId: String, artwork: Artwork): Mono<Artwork>
    fun existsById(id: String): Mono<Boolean>
    fun updateStatusByIdAndPreviousStatus(
        artworkId: String,
        prevStatus: Artwork.ArtworkStatus,
        newStatus: Artwork.ArtworkStatus
    ): Mono<Artwork>
}
