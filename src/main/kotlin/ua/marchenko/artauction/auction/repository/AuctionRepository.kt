package ua.marchenko.artauction.auction.repository

import ua.marchenko.artauction.auction.model.Auction

interface AuctionRepository {

    fun save(auction: Auction): Auction

    fun getByIdOrNull(id: String): Auction?

    fun getAll(): List<Auction>

}