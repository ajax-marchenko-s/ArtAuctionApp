package ua.marchenko.artauction.auction.repository

import ua.marchenko.artauction.auction.model.MongoAuction
import ua.marchenko.artauction.auction.model.projection.AuctionFull

interface AuctionRepository {
    fun save(auction: MongoAuction): MongoAuction
    fun findById(id: String): MongoAuction?
    fun findFullById(id: String): AuctionFull?
    fun findAll(): List<MongoAuction>
    fun findFullAll(): List<AuctionFull>
}
