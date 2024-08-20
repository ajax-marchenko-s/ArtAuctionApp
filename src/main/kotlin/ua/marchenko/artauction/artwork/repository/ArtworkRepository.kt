package ua.marchenko.artauction.artwork.repository

import org.bson.types.ObjectId
import ua.marchenko.artauction.artwork.model.Artwork

interface ArtworkRepository {
    fun save(auction: Artwork): Artwork
    fun findById(id: ObjectId): Artwork?
    fun findAll(): List<Artwork>
    fun existsById(id: ObjectId): Boolean
}
