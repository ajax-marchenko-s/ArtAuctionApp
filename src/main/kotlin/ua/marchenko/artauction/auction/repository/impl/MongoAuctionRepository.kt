package ua.marchenko.artauction.auction.repository.impl

import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.Aggregation.group
import org.springframework.data.mongodb.core.aggregation.Aggregation.lookup
import org.springframework.data.mongodb.core.aggregation.Aggregation.match
import org.springframework.data.mongodb.core.aggregation.Aggregation.project
import org.springframework.data.mongodb.core.aggregation.Aggregation.replaceRoot
import org.springframework.data.mongodb.core.aggregation.Aggregation.unwind
import org.springframework.data.mongodb.core.aggregation.AggregationOperation
import org.springframework.data.mongodb.core.aggregation.Fields
import org.springframework.data.mongodb.core.aggregation.ObjectOperators
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository
import ua.marchenko.artauction.artwork.model.MongoArtwork
import ua.marchenko.artauction.artwork.model.projection.ArtworkFull
import ua.marchenko.artauction.auction.model.MongoAuction
import ua.marchenko.artauction.auction.model.projection.AuctionFull
import ua.marchenko.artauction.auction.repository.AuctionRepository

@Repository
internal class MongoAuctionRepository(private val mongoTemplate: MongoTemplate) : AuctionRepository {

    override fun save(auction: MongoAuction): MongoAuction = mongoTemplate.save(auction)

    override fun findById(id: String): MongoAuction? {
        val query = Query.query(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(id))
        return mongoTemplate.findOne(query, MongoAuction::class.java)
    }

    override fun findFullById(id: String): AuctionFull? {
        val aggregation = Aggregation.newAggregation(
            match(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(id)),
            *aggregateFullBuyers().toTypedArray(),
            *aggregateFullArtwork().toTypedArray(),
        )
        val results = mongoTemplate.aggregate(aggregation, "auction", AuctionFull::class.java)
        return results.mappedResults.firstOrNull()
    }

    override fun findAll(): List<MongoAuction> = mongoTemplate.findAll(MongoAuction::class.java)

    override fun findFullAll(): List<AuctionFull> {
        val aggregation = Aggregation.newAggregation(
            *aggregateFullBuyers().toTypedArray(),
            *aggregateFullArtwork().toTypedArray(),
        )
        val results = mongoTemplate.aggregate(aggregation, "auction", AuctionFull::class.java)
        return results.mappedResults.toList()
    }

    private fun aggregateFullArtwork(): List<AggregationOperation> {
        return listOf(
            lookup("artwork", MongoAuction::artworkId.name, Fields.UNDERSCORE_ID, AuctionFull::artwork.name),
            unwind(AuctionFull::artwork.name),
            lookup(
                "user",
                "${AuctionFull::artwork.name}.${MongoArtwork::artistId.name}",
                Fields.UNDERSCORE_ID,
                "${AuctionFull::artwork.name}.${ArtworkFull::artist.name}"
            ),
            project().andExclude(
                "${AuctionFull::artwork.name}.${MongoArtwork::artistId.name}",
                MongoAuction::artworkId.name,
            ),
            unwind("${AuctionFull::artwork.name}.${ArtworkFull::artist.name}")
        )
    }

    private fun aggregateFullBuyers(): List<AggregationOperation> {
        return listOf(
            unwind(MongoAuction::buyers.name),
            lookup(
                "user",
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
        )
    }
}
