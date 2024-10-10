package ua.marchenko.artauction.artwork.repository.impl

import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.Aggregation.lookup
import org.springframework.data.mongodb.core.aggregation.Aggregation.match
import org.springframework.data.mongodb.core.aggregation.Aggregation.project
import org.springframework.data.mongodb.core.aggregation.Aggregation.unwind
import org.springframework.data.mongodb.core.aggregation.Fields
import org.springframework.data.mongodb.core.aggregation.FieldsExposingAggregationOperation
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ua.marchenko.artauction.artwork.model.MongoArtwork
import ua.marchenko.artauction.artwork.model.projection.ArtworkFull
import ua.marchenko.artauction.artwork.repository.ArtworkRepository
import ua.marchenko.artauction.user.model.MongoUser

@Repository
@Suppress("SpreadOperator")
internal class MongoArtworkRepository(
    private val reactiveMongoTemplate: ReactiveMongoTemplate
) : ArtworkRepository {

    override fun save(artwork: MongoArtwork): Mono<MongoArtwork> = reactiveMongoTemplate.save(artwork)

    override fun findById(id: String): Mono<MongoArtwork> {
        val query = Query.query(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(id))
        return reactiveMongoTemplate.findOne(query, MongoArtwork::class.java)
    }

    override fun findFullById(id: String): Mono<ArtworkFull> {
        val aggregation = Aggregation.newAggregation(
            match(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(id)),
            *aggregateFullArtist(),
        )
        return reactiveMongoTemplate.aggregate(aggregation, MongoArtwork.COLLECTION, ArtworkFull::class.java)
            .singleOrEmpty()
    }

    override fun findAll(page: Int, limit: Int): Flux<MongoArtwork> {
        val skip = page * limit
        val query = Query().skip(skip.toLong()).limit(limit)
        return reactiveMongoTemplate.find(query, MongoArtwork::class.java)
    }

    override fun findFullAll(page: Int, limit: Int): Flux<ArtworkFull> {
        val skip = page * limit
        val aggregation = Aggregation.newAggregation(
            Aggregation.skip(skip.toLong()),
            Aggregation.limit(limit.toLong()),
            *aggregateFullArtist(),
        )
        return reactiveMongoTemplate.aggregate(aggregation, MongoArtwork.COLLECTION, ArtworkFull::class.java)
    }

    override fun existsById(id: String): Mono<Boolean> {
        val query = Query.query(Criteria.where(MongoArtwork::id.name).isEqualTo(id))
        return reactiveMongoTemplate.exists(query, MongoArtwork::class.java)
    }

    private fun aggregateFullArtist(): Array<FieldsExposingAggregationOperation> {
        return listOf(
            lookup(MongoUser.COLLECTION, MongoArtwork::artistId.name, Fields.UNDERSCORE_ID, ArtworkFull::artist.name),
            project().andExclude(MongoArtwork::artistId.name),
            unwind(ArtworkFull::artist.name)
        ).toTypedArray()
    }
}
