package ua.marchenko.artauction.auction.repository.impl

import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.Aggregation.group
import org.springframework.data.mongodb.core.aggregation.Aggregation.lookup
import org.springframework.data.mongodb.core.aggregation.Aggregation.match
import org.springframework.data.mongodb.core.aggregation.Aggregation.project
import org.springframework.data.mongodb.core.aggregation.Aggregation.replaceRoot
import org.springframework.data.mongodb.core.aggregation.Aggregation.unwind
import org.springframework.data.mongodb.core.aggregation.Fields
import org.springframework.data.mongodb.core.aggregation.FieldsExposingAggregationOperation
import org.springframework.data.mongodb.core.aggregation.ObjectOperators
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ua.marchenko.artauction.artwork.model.MongoArtwork
import ua.marchenko.artauction.artwork.model.projection.ArtworkFull
import ua.marchenko.artauction.auction.model.MongoAuction
import ua.marchenko.artauction.auction.model.projection.AuctionFull
import ua.marchenko.artauction.auction.repository.AuctionRepository
import ua.marchenko.artauction.user.model.MongoUser

@Repository
@Suppress("SpreadOperator")
internal class MongoAuctionRepository(
    private val reactiveMongoTemplate: ReactiveMongoTemplate
) : AuctionRepository {

    override fun save(auction: MongoAuction): Mono<MongoAuction> = reactiveMongoTemplate.save(auction)

    override fun findById(id: String): Mono<MongoAuction> {
        val query = Query.query(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(id))
        return reactiveMongoTemplate.findOne(query, MongoAuction::class.java)
    }

    override fun findFullById(id: String): Mono<AuctionFull> {
        val aggregation = Aggregation.newAggregation(
            match(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(id)),
            *aggregateFullBuyers(),
            *aggregateFullArtwork(),
        )
        return reactiveMongoTemplate.aggregate(aggregation, MongoAuction.COLLECTION, AuctionFull::class.java)
            .singleOrEmpty()
    }

    override fun findAll(page: Int, limit: Int): Flux<MongoAuction> {
        val skip = page * limit
        val query = Query().skip(skip.toLong()).limit(limit)
        return reactiveMongoTemplate.find(query, MongoAuction::class.java)
    }

    override fun findFullAll(page: Int, limit: Int): Flux<AuctionFull> {
        val skip = page * limit
        val aggregation = Aggregation.newAggregation(
            Aggregation.skip(skip.toLong()),
            Aggregation.limit(limit.toLong()),
            *aggregateFullBuyers(),
            *aggregateFullArtwork(),
        )
        return reactiveMongoTemplate.aggregate(aggregation, MongoAuction.COLLECTION, AuctionFull::class.java)
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
                "${AuctionFull::artwork.name}.${ArtworkFull::artist.name}"
            ),
            project().andExclude(
                "${AuctionFull::artwork.name}.${MongoArtwork::artistId.name}",
                MongoAuction::artworkId.name,
            ),
            unwind("${AuctionFull::artwork.name}.${ArtworkFull::artist.name}")
        ).toTypedArray()
    }

    private fun aggregateFullBuyers(): Array<FieldsExposingAggregationOperation> {
        return listOf(
            unwind(MongoAuction::buyers.name),
            lookup(
                MongoUser.COLLECTION,
                "${MongoAuction::buyers.name}.${MongoAuction.Bid::buyerId.name}",
                Fields.UNDERSCORE_ID,
                "${MongoAuction::buyers.name}.${AuctionFull.BidFull::buyer.name}"
            ),
            unwind("${MongoAuction::buyers.name}.${AuctionFull.BidFull::buyer.name}"),
            group(Fields.UNDERSCORE_ID)
                .push(MongoAuction::buyers.name).`as`(MongoAuction::buyers.name)
                .first(Aggregation.ROOT).`as`("mainData"),
            replaceRoot().withValueOf(
                ObjectOperators.valueOf("mainData")
                    .mergeWith(
                        mapOf(MongoAuction::buyers.name to "\$${MongoAuction::buyers.name}")
                    )
            ),
            project().andExclude(
                "${MongoAuction::buyers.name}.${MongoAuction.Bid::buyerId.name}"
            ),
        ).toTypedArray()
    }
}
