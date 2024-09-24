package ua.marchenko.artauction.artwork.repository.impl

import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.Aggregation.lookup
import org.springframework.data.mongodb.core.aggregation.Aggregation.match
import org.springframework.data.mongodb.core.aggregation.Aggregation.project
import org.springframework.data.mongodb.core.aggregation.Aggregation.unwind
import org.springframework.data.mongodb.core.aggregation.AggregationOperation
import org.springframework.data.mongodb.core.aggregation.Fields
import org.springframework.stereotype.Repository
import ua.marchenko.artauction.artwork.model.MongoArtwork
import ua.marchenko.artauction.artwork.model.projection.ArtworkFull
import ua.marchenko.artauction.artwork.repository.ArtworkRepository

@Repository
internal class MongoArtworkRepository(private val mongoTemplate: MongoTemplate) : ArtworkRepository {

    override fun save(artwork: MongoArtwork): MongoArtwork = mongoTemplate.save(artwork)

    override fun findById(id: String): MongoArtwork? {
        val query = Query.query(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(id))
        return mongoTemplate.findOne(query, MongoArtwork::class.java)
    }

    override fun findFullById(id: String): ArtworkFull? {
        val aggregation = Aggregation.newAggregation(
            match(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(id)),
            *aggregateFullArtist().toTypedArray(),
        )
        val results = mongoTemplate.aggregate(aggregation, "artwork", ArtworkFull::class.java)
        return results.mappedResults.firstOrNull()
    }

    override fun findAll(): List<MongoArtwork> = mongoTemplate.findAll(MongoArtwork::class.java)

    override fun findFullAll(): List<ArtworkFull> {
        val aggregation = Aggregation.newAggregation(
            *aggregateFullArtist().toTypedArray(),
        )
        val results = mongoTemplate.aggregate(aggregation, "artwork", ArtworkFull::class.java)
        return results.mappedResults.toList()
    }

    override fun existsById(id: String): Boolean {
        val query = Query.query(Criteria.where(MongoArtwork::id.name).isEqualTo(id))
        return mongoTemplate.exists(query, MongoArtwork::class.java)
    }

    private fun aggregateFullArtist(): List<AggregationOperation> {
        return listOf(
            lookup("user", MongoArtwork::artistId.name, Fields.UNDERSCORE_ID, ArtworkFull::artist.name),
            project().andExclude(MongoArtwork::artistId.name),
            unwind(ArtworkFull::artist.name)
        )
    }
}
