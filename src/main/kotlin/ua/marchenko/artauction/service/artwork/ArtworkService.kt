package ua.marchenko.artauction.service.artwork

import ua.marchenko.artauction.dto.artwork.ArtworkRequest
import ua.marchenko.artauction.dto.artwork.ArtworkResponse

interface ArtworkService {
    fun findAll(): List<ArtworkResponse>
    fun findById(id: String): ArtworkResponse
    fun save(artworkRequest: ArtworkRequest): ArtworkResponse
}