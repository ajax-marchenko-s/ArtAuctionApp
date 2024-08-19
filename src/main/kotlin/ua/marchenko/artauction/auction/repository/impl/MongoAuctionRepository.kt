package ua.marchenko.artauction.auction.repository.impl

import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import ua.marchenko.artauction.auction.model.Auction
import ua.marchenko.artauction.auction.repository.AuctionRepository

@Repository
class MongoAuctionRepository(private val mongoTemplate: MongoTemplate) : AuctionRepository {
    override fun save(auction: Auction): Auction = mongoTemplate.save(auction)

    override fun getByIdOrNull(id: String): Auction? {
        val query = Query.query(Criteria.where("id").`is`(id))
        return mongoTemplate.findOne(query, Auction::class.java)
    }

    override fun getAll(): List<Auction> = mongoTemplate.findAll(Auction::class.java)

}
