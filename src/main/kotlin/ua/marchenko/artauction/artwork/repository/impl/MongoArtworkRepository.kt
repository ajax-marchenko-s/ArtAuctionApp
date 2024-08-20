package ua.marchenko.artauction.artwork.repository.impl

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository
import ua.marchenko.artauction.artwork.model.Artwork
import ua.marchenko.artauction.artwork.repository.ArtworkRepository

@Repository
class MongoArtworkRepository(private val mongoTemplate: MongoTemplate) : ArtworkRepository {
    override fun save(auction: Artwork) = mongoTemplate.save(auction)

    override fun findById(id: ObjectId): Artwork? {
        val query = Query.query(Criteria.where("id").isEqualTo(id))
        return mongoTemplate.findOne(query, Artwork::class.java)
    }

    override fun findAll(): List<Artwork> = mongoTemplate.findAll(Artwork::class.java)

    override fun existsById(id: ObjectId): Boolean {
        val query = Query.query(Criteria.where("id").isEqualTo(id))
        return mongoTemplate.exists(query, Artwork::class.java)
    }
}
