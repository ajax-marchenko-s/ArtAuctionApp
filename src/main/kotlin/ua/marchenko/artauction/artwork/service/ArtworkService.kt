package ua.marchenko.artauction.artwork.service

import ua.marchenko.artauction.artwork.enums.ArtworkStatus
import ua.marchenko.artauction.artwork.model.MongoArtwork
import ua.marchenko.artauction.artwork.model.projection.ArtworkFull

interface ArtworkService {
    fun getAll(page: Int = 1, limit: Int = 10): List<MongoArtwork>
    fun getFullAll(page: Int = 1, limit: Int = 10): List<ArtworkFull>
    fun getById(id: String): MongoArtwork
    fun getFullById(id: String): ArtworkFull
    fun save(artwork: MongoArtwork): MongoArtwork
    fun update(artworkId: String, mongoArtwork: MongoArtwork): MongoArtwork
    fun updateStatus(artworkId: String, status: ArtworkStatus): MongoArtwork
    fun existsById(id: String): Boolean
}
