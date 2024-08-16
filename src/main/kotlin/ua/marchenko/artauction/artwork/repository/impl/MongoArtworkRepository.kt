package ua.marchenko.artauction.artwork.repository.impl

import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import ua.marchenko.artauction.artwork.model.Artwork
import ua.marchenko.artauction.artwork.repository.ArtworkRepository

@Repository
class MongoArtworkRepository(private val mongoTemplate: MongoTemplate) : ArtworkRepository {

    override fun save(auction: Artwork): Artwork {
        return mongoTemplate.save(auction)
    }

    override fun getByIdOrNull(id: String): Artwork? {
        val query = Query.query(Criteria.where("id").`is`(id))
        return mongoTemplate.findOne(query, Artwork::class.java)
    }

    override fun getAll(): List<Artwork> {
        return mongoTemplate.findAll(Artwork::class.java)
    }

    override fun existsById(id: String): Boolean {
        val query = Query.query(Criteria.where("id").`is`(id))
        return mongoTemplate.exists(query, Artwork::class.java)
    }

}