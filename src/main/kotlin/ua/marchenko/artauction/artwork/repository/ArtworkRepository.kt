package ua.marchenko.artauction.artwork.repository

import ua.marchenko.artauction.artwork.model.Artwork

interface ArtworkRepository {
    fun save(auction: Artwork): Artwork
    fun findById(id: String): Artwork?
    fun findAll(): List<Artwork>
    fun existsById(id: String): Boolean
}
