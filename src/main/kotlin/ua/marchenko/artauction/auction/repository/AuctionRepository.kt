package ua.marchenko.artauction.auction.repository

import ua.marchenko.artauction.auction.model.Auction

interface AuctionRepository {
    fun save(auction: Auction): Auction
    fun findById(id: String): Auction?
    fun findAll(): List<Auction>
}
