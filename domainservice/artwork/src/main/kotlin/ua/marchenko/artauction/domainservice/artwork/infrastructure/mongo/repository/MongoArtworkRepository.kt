package ua.marchenko.artauction.domainservice.artwork.infrastructure.mongo.repository

import kotlin.reflect.full.memberProperties
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.Aggregation.lookup
import org.springframework.data.mongodb.core.aggregation.Aggregation.match
import org.springframework.data.mongodb.core.aggregation.Aggregation.project
import org.springframework.data.mongodb.core.aggregation.Aggregation.unwind
import org.springframework.data.mongodb.core.aggregation.Fields
import org.springframework.data.mongodb.core.aggregation.FieldsExposingAggregationOperation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ua.marchenko.artauction.domainservice.user.infrastructure.mongo.entity.MongoUser
import ua.marchenko.artauction.domainservice.artwork.application.port.output.ArtworkRepositoryOutputPort
import ua.marchenko.artauction.domainservice.artwork.domain.Artwork
import ua.marchenko.artauction.domainservice.artwork.domain.CreateArtwork
import ua.marchenko.artauction.domainservice.artwork.domain.projection.ArtworkFull
import ua.marchenko.artauction.domainservice.artwork.infrastructure.mongo.entity.projection.MongoArtworkFull as MongoArtworkFull
import ua.marchenko.artauction.domainservice.artwork.infrastructure.mongo.entity.MongoArtwork
import ua.marchenko.artauction.domainservice.artwork.infrastructure.mongo.mapper.getMongoField
import ua.marchenko.artauction.domainservice.artwork.infrastructure.mongo.mapper.toDomain
import ua.marchenko.artauction.domainservice.artwork.infrastructure.mongo.mapper.toMongo
import ua.marchenko.artauction.domainservice.artwork.infrastructure.mongo.mapper.toMongoStatus

@Repository
class MongoArtworkRepository(
    private val reactiveMongoTemplate: ReactiveMongoTemplate,
) : ArtworkRepositoryOutputPort {

    override fun save(artwork: CreateArtwork): Mono<Artwork> =
        reactiveMongoTemplate.save(artwork.toMongo()).map { it.toDomain() }

    override fun findById(id: String): Mono<Artwork> {
        val query = Query.query(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(id))
        return reactiveMongoTemplate.findOne(query, MongoArtwork::class.java).map { it.toDomain() }
    }

    @Suppress("SpreadOperator")
    override fun findFullById(id: String): Mono<ArtworkFull> {
        val aggregation = Aggregation.newAggregation(
            match(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(id)),
            *aggregateFullArtist(),
        )
        return reactiveMongoTemplate.aggregate(aggregation, MongoArtwork.COLLECTION, MongoArtworkFull::class.java)
            .singleOrEmpty().map { it.toDomain() }
    }

    override fun findAll(page: Int, limit: Int): Flux<Artwork> {
        val skip = page * limit
        val query = Query().skip(skip.toLong()).limit(limit)
        return reactiveMongoTemplate.find(query, MongoArtwork::class.java).map { it.toDomain() }
    }

    @Suppress("SpreadOperator")
    override fun findFullAll(page: Int, limit: Int): Flux<ArtworkFull> {
        val skip = page * limit
        val aggregation = Aggregation.newAggregation(
            Aggregation.skip(skip.toLong()),
            Aggregation.limit(limit.toLong()),
            *aggregateFullArtist(),
        )
        return reactiveMongoTemplate.aggregate(aggregation, MongoArtwork.COLLECTION, MongoArtworkFull::class.java)
            .map { it.toDomain() }
    }

    override fun existsById(id: String): Mono<Boolean> {
        val query = Query.query(Criteria.where(MongoArtwork::id.name).isEqualTo(id))
        return reactiveMongoTemplate.exists(query, MongoArtwork::class.java)
    }

    override fun updateStatusByIdAndPreviousStatus(
        id: String,
        prevStatus: Artwork.ArtworkStatus,
        newStatus: Artwork.ArtworkStatus
    ): Mono<Artwork> {
        val query = Query.query(
            Criteria.where(MongoArtwork::id.name).isEqualTo(id).and(MongoArtwork::status.name)
                .isEqualTo(prevStatus.toMongoStatus())
        )
        val changes = Update.update(MongoArtwork::status.name, newStatus.toMongoStatus())
        val options = FindAndModifyOptions.options().returnNew(true)
        return reactiveMongoTemplate.findAndModify(query, changes, options, MongoArtwork::class.java)
            .map { it.toDomain() }
    }

    override fun updateById(id: String, artwork: Artwork, nonUpdatableFields: List<String>): Mono<Artwork> {
        val mongoArtwork = artwork.toMongo()
        val query = Query.query(Criteria.where(MongoArtwork::id.name).isEqualTo(id))
        val changes = Update()
        MongoArtwork::class.memberProperties
            .filter {
                val mongoField = getMongoField(it.name)
                mongoField != null && !nonUpdatableFields.contains(getMongoField(it.name))
            }
            .forEach { property ->
                property.get(mongoArtwork)?.let { value ->
                    changes.set(property.name, value)
                }
            }
        val options = FindAndModifyOptions.options().returnNew(true)
        return reactiveMongoTemplate.findAndModify(query, changes, options, MongoArtwork::class.java)
            .map { it.toDomain() }
    }

    private fun aggregateFullArtist(): Array<FieldsExposingAggregationOperation> {
        return listOf(
            lookup(MongoUser.COLLECTION, MongoArtwork::artistId.name, Fields.UNDERSCORE_ID, ArtworkFull::artist.name),
            project().andExclude(MongoArtwork::artistId.name),
            unwind(ArtworkFull::artist.name)
        ).toTypedArray()
    }
}
