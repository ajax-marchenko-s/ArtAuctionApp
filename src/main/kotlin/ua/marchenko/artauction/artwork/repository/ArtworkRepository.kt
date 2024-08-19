package ua.marchenko.artauction.artwork.repository

import ua.marchenko.artauction.artwork.model.Artwork

interface ArtworkRepository {

    fun save(auction: Artwork): Artwork

    fun getByIdOrNull(id: String): Artwork?

    fun getAll(): List<Artwork>

    fun existsById(id: String): Boolean
}
