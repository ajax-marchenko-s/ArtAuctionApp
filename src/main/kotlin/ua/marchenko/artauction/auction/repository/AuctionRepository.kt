package ua.marchenko.artauction.auction.repository

import org.bson.types.ObjectId
import ua.marchenko.artauction.auction.model.Auction

interface AuctionRepository {
    fun save(auction: Auction): Auction
    fun findById(id: ObjectId): Auction?
    fun findAll(): List<Auction>
}
