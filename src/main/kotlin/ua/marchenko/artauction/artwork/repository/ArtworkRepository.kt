package ua.marchenko.artauction.artwork.repository

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ua.marchenko.artauction.artwork.model.MongoArtwork
import ua.marchenko.artauction.artwork.model.projection.ArtworkFull

interface ArtworkRepository {
    fun save(artwork: MongoArtwork): Mono<MongoArtwork>
    fun findById(id: String): Mono<MongoArtwork>
    fun findFullById(id: String): Mono<ArtworkFull>
    fun findAll(page: Int = 0, limit: Int = 10): Flux<MongoArtwork>
    fun findFullAll(page: Int = 0, limit: Int = 10): Flux<ArtworkFull>
    fun existsById(id: String): Mono<Boolean>
}
