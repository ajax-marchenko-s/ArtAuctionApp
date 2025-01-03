package ua.marchenko.artauction.domainservice.artwork.application.port.output

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ua.marchenko.artauction.domainservice.artwork.domain.Artwork
import ua.marchenko.artauction.domainservice.artwork.domain.Artwork.ArtworkStatus
import ua.marchenko.artauction.domainservice.artwork.domain.CreateArtwork
import ua.marchenko.artauction.domainservice.artwork.domain.projection.ArtworkFull

interface ArtworkRepositoryOutputPort {
    fun save(artwork: CreateArtwork): Mono<Artwork>
    fun save(artwork: Artwork): Mono<Artwork>
    fun findById(id: String): Mono<Artwork>
    fun findFullById(id: String): Mono<ArtworkFull>
    fun findAll(page: Int = 0, limit: Int = 10): Flux<Artwork>
    fun findFullAll(page: Int = 0, limit: Int = 10): Flux<ArtworkFull>
    fun existsById(id: String): Mono<Boolean>
    fun updateStatusByIdAndPreviousStatus(
        id: String,
        prevStatus: ArtworkStatus,
        newStatus: ArtworkStatus,
    ): Mono<Artwork>
}
