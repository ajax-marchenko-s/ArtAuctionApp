package ua.marchenko.artauction.domainservice.auction.infrastructure.mongo.repository

import org.bson.Document
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.Aggregation.addFields
import org.springframework.data.mongodb.core.aggregation.Aggregation.group
import org.springframework.data.mongodb.core.aggregation.Aggregation.lookup
import org.springframework.data.mongodb.core.aggregation.Aggregation.match
import org.springframework.data.mongodb.core.aggregation.Aggregation.project
import org.springframework.data.mongodb.core.aggregation.Aggregation.replaceRoot
import org.springframework.data.mongodb.core.aggregation.Aggregation.unwind
import org.springframework.data.mongodb.core.aggregation.ComparisonOperators
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators
import org.springframework.data.mongodb.core.aggregation.Fields
import org.springframework.data.mongodb.core.aggregation.FieldsExposingAggregationOperation
import org.springframework.data.mongodb.core.aggregation.ObjectOperators
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ua.marchenko.artauction.domainservice.auction.application.port.output.AuctionRepositoryOutputPort
import ua.marchenko.artauction.domainservice.auction.domain.Auction
import ua.marchenko.artauction.domainservice.auction.domain.projection.AuctionFull
import ua.marchenko.artauction.domainservice.auction.infrastructure.mongo.entity.MongoAuction
import ua.marchenko.artauction.domainservice.auction.infrastructure.mongo.entity.projection.MongoAuctionFull
import ua.marchenko.artauction.domainservice.auction.infrastructure.mongo.mapper.toDomain
import ua.marchenko.artauction.domainservice.auction.infrastructure.mongo.mapper.toMongo
import ua.marchenko.artauction.domainservice.user.infrastructure.mongo.entity.MongoUser
import ua.marchenko.artauction.domainservice.artwork.infrastructure.mongo.entity.MongoArtwork
import ua.marchenko.artauction.domainservice.artwork.infrastructure.mongo.entity.projection.MongoArtworkFull
import ua.marchenko.artauction.domainservice.auction.domain.CreateAuction

@Repository
class MongoAuctionRepository(
    private val reactiveMongoTemplate: ReactiveMongoTemplate,
) : AuctionRepositoryOutputPort {

    override fun save(auction: CreateAuction): Mono<Auction> =
        reactiveMongoTemplate.save(auction.toMongo()).map { it.toDomain() }

    override fun findById(id: String): Mono<Auction> {
        val query = Query.query(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(id))
        return reactiveMongoTemplate.findOne(query, MongoAuction::class.java).map { it.toDomain() }
    }

    @Suppress("SpreadOperator")
    override fun findFullById(id: String): Mono<AuctionFull> {
        val aggregation = Aggregation.newAggregation(
            match(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(id)),
            *aggregateFullBuyers(),
            *aggregateFullArtwork(),
        )
        return reactiveMongoTemplate.aggregate(aggregation, MongoAuction.COLLECTION, MongoAuctionFull::class.java)
            .singleOrEmpty().map { it.toDomain() }
    }

    override fun findAll(page: Int, limit: Int): Flux<Auction> {
        val skip = page * limit
        val query = Query().skip(skip.toLong()).limit(limit)
        return reactiveMongoTemplate.find(query, MongoAuction::class.java).map { it.toDomain() }
    }

    @Suppress("SpreadOperator")
    override fun findFullAll(page: Int, limit: Int): Flux<AuctionFull> {
        val skip = page * limit
        val aggregation = Aggregation.newAggregation(
            Aggregation.skip(skip.toLong()),
            Aggregation.limit(limit.toLong()),
            *aggregateFullBuyers(),
            *aggregateFullArtwork(),
        )
        return reactiveMongoTemplate.aggregate(aggregation, MongoAuction.COLLECTION, MongoAuctionFull::class.java)
            .map { it.toDomain() }
    }

    private fun aggregateFullArtwork(): Array<FieldsExposingAggregationOperation> {
        return listOf(
            lookup(
                MongoArtwork.COLLECTION,
                MongoAuction::artworkId.name,
                Fields.UNDERSCORE_ID,
                AuctionFull::artwork.name
            ),
            unwind(AuctionFull::artwork.name),
            lookup(
                MongoUser.COLLECTION,
                "${AuctionFull::artwork.name}.${MongoArtwork::artistId.name}",
                Fields.UNDERSCORE_ID,
                "${AuctionFull::artwork.name}.${MongoArtworkFull::artist.name}"
            ),
            project().andExclude(
                "${AuctionFull::artwork.name}.${MongoArtwork::artistId.name}",
                MongoAuction::artworkId.name,
            ),
            unwind("${AuctionFull::artwork.name}.${MongoArtworkFull::artist.name}")
        ).toTypedArray()
    }

    private fun aggregateFullBuyers(): Array<FieldsExposingAggregationOperation> {
        return listOf(
            unwind(MongoAuction::buyers.name, true),
            lookup(
                MongoUser.COLLECTION,
                "${MongoAuction::buyers.name}.${MongoAuction.Bid::buyerId.name}",
                Fields.UNDERSCORE_ID,
                "${MongoAuction::buyers.name}.${AuctionFull.BidFull::buyer.name}"
            ),
            unwind("${MongoAuction::buyers.name}.${AuctionFull.BidFull::buyer.name}", true),
            group(Fields.UNDERSCORE_ID)
                .push(
                    ConditionalOperators.`when`(
                        ComparisonOperators.Eq.valueOf(MongoAuction::buyers.name).equalToValue(Document())
                    )
                        .then(DEFAULT)
                        .otherwise("\$${MongoAuction::buyers.name}")
                ).`as`(MongoAuction::buyers.name)
                .first(Aggregation.ROOT).`as`("mainData"),
            replaceRoot().withValueOf(
                ObjectOperators.valueOf("mainData")
                    .mergeWith(
                        mapOf(MongoAuction::buyers.name to "\$${MongoAuction::buyers.name}")
                    )
            ),
            addFields()
                .addField(MongoAuction::buyers.name)
                .withValue(
                    ConditionalOperators.Cond.`when`(
                        ComparisonOperators.Eq.valueOf(MongoAuction::buyers.name).equalToValue(listOf(DEFAULT))
                    ).then(listOf<Any>()).otherwise("\$${MongoAuction::buyers.name}")
                ).build(),
            project().andExclude("${MongoAuction::buyers.name}.${MongoAuction.Bid::buyerId.name}"),
        ).toTypedArray()
    }

    companion object {
        private const val DEFAULT = "DEFAULT"
    }
}
