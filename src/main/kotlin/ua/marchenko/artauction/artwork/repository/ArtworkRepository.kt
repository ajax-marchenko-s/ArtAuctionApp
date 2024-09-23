package ua.marchenko.artauction.artwork.repository

import ua.marchenko.artauction.artwork.model.MongoArtwork
import ua.marchenko.artauction.artwork.model.projection.ArtworkFull

interface ArtworkRepository {
    fun save(artwork: MongoArtwork): MongoArtwork
    fun findById(id: String): MongoArtwork?
    fun findFullById(id: String): ArtworkFull?
    fun findAll(): List<MongoArtwork>
    fun findFullAll(): List<ArtworkFull>
    fun existsById(id: String): Boolean
}
