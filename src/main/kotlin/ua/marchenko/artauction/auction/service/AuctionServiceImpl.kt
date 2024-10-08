package ua.marchenko.artauction.auction.service

import org.springframework.stereotype.Service
import ua.marchenko.artauction.artwork.enums.ArtworkStatus
import ua.marchenko.artauction.auction.controller.dto.CreateAuctionRequest
import ua.marchenko.artauction.auction.controller.dto.AuctionResponse
import ua.marchenko.artauction.auction.repository.AuctionRepository
import ua.marchenko.artauction.artwork.service.ArtworkService
import ua.marchenko.artauction.auction.exception.AuctionNotFoundException
import ua.marchenko.artauction.auction.exception.InvalidAuctionOperationException
import ua.marchenko.artauction.auction.mapper.toMongo
import ua.marchenko.artauction.auction.mapper.toResponse
import ua.marchenko.artauction.auction.model.MongoAuction
import ua.marchenko.artauction.auction.model.projection.AuctionFull

@Service
class AuctionServiceImpl(
    private val auctionRepository: AuctionRepository,
    private val artworkService: ArtworkService,
) : AuctionService {

    override fun getAll(page: Int, limit: Int): List<MongoAuction> = auctionRepository.findAll(page, limit)

    override fun getFullAll(page: Int, limit: Int): List<AuctionFull> = auctionRepository.findFullAll(page, limit)

    override fun getById(id: String) = auctionRepository.findById(id) ?: throw AuctionNotFoundException(id)

    override fun getFullById(id: String) = auctionRepository.findFullById(id) ?: throw AuctionNotFoundException(id)

    override fun save(auction: CreateAuctionRequest): AuctionResponse {
        val artwork = artworkService.getById(auction.artworkId)
        if (artwork.status != ArtworkStatus.VIEW) {
            throw InvalidAuctionOperationException("Trying to create auction with non-VIEW artwork")
        }
        artworkService.updateStatus(auction.artworkId, ArtworkStatus.ON_AUCTION)
        return auctionRepository.save(auction.toMongo()).toResponse()
    }
}
