package ua.marchenko.artauction.repository.auction

import org.springframework.data.mongodb.repository.MongoRepository
import ua.marchenko.artauction.model.Auction

interface MongoAuctionRepository: MongoRepository<Auction, String>{

}