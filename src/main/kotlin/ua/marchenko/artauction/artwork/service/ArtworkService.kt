package ua.marchenko.artauction.artwork.service

import ua.marchenko.artauction.artwork.enums.ArtworkStatus
import ua.marchenko.artauction.artwork.model.Artwork

interface ArtworkService {
    fun getAll(): List<Artwork>
    fun getById(id: String): Artwork
    fun save(artwork: Artwork): Artwork
    fun update(artworkId: String, artwork: Artwork): Artwork
    fun updateStatus(artworkId: String, status: ArtworkStatus): Artwork
    fun existsById(id: String): Boolean
}
