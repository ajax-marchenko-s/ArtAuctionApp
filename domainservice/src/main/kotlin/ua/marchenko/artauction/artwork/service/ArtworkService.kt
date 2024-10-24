package ua.marchenko.artauction.artwork.service

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ua.marchenko.artauction.artwork.model.MongoArtwork
import ua.marchenko.artauction.artwork.model.projection.ArtworkFull
import ua.marchenko.core.artwork.enums.ArtworkStatus

interface ArtworkService {
    fun getAll(page: Int = 0, limit: Int = 10): Flux<MongoArtwork>
    fun getFullAll(page: Int = 0, limit: Int = 10): Flux<ArtworkFull>
    fun getById(id: String): Mono<MongoArtwork>
    fun getFullById(id: String): Mono<ArtworkFull>
    fun save(artwork: MongoArtwork): Mono<MongoArtwork>
    fun update(artworkId: String, artwork: MongoArtwork): Mono<MongoArtwork>
    fun existsById(id: String): Mono<Boolean>
    fun updateStatusByIdAndPreviousStatus(
        artworkId: String,
        prevStatus: ArtworkStatus,
        newStatus: ArtworkStatus
    ): Mono<MongoArtwork>
}
