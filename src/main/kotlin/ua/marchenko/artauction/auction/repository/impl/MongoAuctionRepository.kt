package ua.marchenko.artauction.auction.repository.impl

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.Aggregation.group
import org.springframework.data.mongodb.core.aggregation.Aggregation.lookup
import org.springframework.data.mongodb.core.aggregation.Aggregation.match
import org.springframework.data.mongodb.core.aggregation.Aggregation.project
import org.springframework.data.mongodb.core.aggregation.Aggregation.replaceRoot
import org.springframework.data.mongodb.core.aggregation.Aggregation.unwind
import org.springframework.data.mongodb.core.aggregation.ObjectOperators
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository
import ua.marchenko.artauction.auction.model.MongoAuction
import ua.marchenko.artauction.auction.model.projection.AuctionFull
import ua.marchenko.artauction.auction.repository.AuctionRepository

@Repository
internal class MongoAuctionRepository(private val mongoTemplate: MongoTemplate) : AuctionRepository {

    override fun save(auction: MongoAuction): MongoAuction = mongoTemplate.save(auction)

    override fun findById(id: String): MongoAuction? {
        val query = Query.query(Criteria.where("id").isEqualTo(id))
        return mongoTemplate.findOne(query, MongoAuction::class.java)
    }

    override fun findFullById(id: String): AuctionFull? {
        val aggregation = Aggregation.newAggregation(
            match(Criteria.where("_id").`is`(ObjectId(id))),
            unwind("buyers"),
            lookup("user", "buyers.buyerId", "_id", "buyers.buyer"),
            unwind("buyers.buyer"),
            group("_id")
                .push("buyers").`as`("buyers")
                .first(Aggregation.ROOT).`as`("mainData"),
            replaceRoot().withValueOf(
                ObjectOperators.valueOf("mainData")
                    .mergeWith(
                        mapOf("buyers" to "\$buyers")
                    )
            ),
            lookup("artwork", "artworkId", "_id", "artwork"),
            unwind("artwork"),
            lookup("user", "artwork.artistId", "_id", "artwork.artist"),
            project().andExclude("artwork.artistId", "artworkId", "buyers.buyerId"),
            unwind("artwork.artist")
        )
        val results = mongoTemplate.aggregate(aggregation, "auction", AuctionFull::class.java)
        return results.mappedResults.firstOrNull()
    }

    override fun findAll(): List<MongoAuction> = mongoTemplate.findAll(MongoAuction::class.java)

    override fun findFullAll(): List<AuctionFull> {
        val aggregation = Aggregation.newAggregation(
            unwind("buyers"),
            lookup("user", "buyers.buyerId", "_id", "buyers.buyer"),
            unwind("buyers.buyer"),
            group("_id")
                .push("buyers").`as`("buyers")
                .first(Aggregation.ROOT).`as`("mainData"),
            replaceRoot().withValueOf(
                ObjectOperators.valueOf("mainData")
                    .mergeWith(
                        mapOf("buyers" to "\$buyers")
                    )
            ),
            lookup("artwork", "artworkId", "_id", "artwork"),
            unwind("artwork"),
            lookup("user", "artwork.artistId", "_id", "artwork.artist"),
            project().andExclude("artwork.artistId", "artworkId", "buyers.buyerId"),
            unwind("artwork.artist")
        )
        val results = mongoTemplate.aggregate(aggregation, "auction", AuctionFull::class.java)
        return results.mappedResults.toList()
    }
}
