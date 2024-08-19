package ua.marchenko.artauction.artwork.service

import ua.marchenko.artauction.artwork.model.Artwork

interface ArtworkService {
    fun findAll(): List<Artwork>
    fun findById(id: String): Artwork
    fun save(artwork: Artwork): Artwork
    fun update(artworkId: String, artwork: Artwork, isStatusUpdated: Boolean = false): Artwork
    fun existsById(id: String): Boolean
}
