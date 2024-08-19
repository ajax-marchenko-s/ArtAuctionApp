package ua.marchenko.artauction.auction.service

import org.springframework.stereotype.Service
import ua.marchenko.artauction.artwork.enums.ArtworkStatus
import ua.marchenko.artauction.auction.controller.dto.AuctionRequest
import ua.marchenko.artauction.auction.controller.dto.AuctionResponse
import ua.marchenko.artauction.auction.repository.AuctionRepository
import ua.marchenko.artauction.artwork.service.ArtworkService
import ua.marchenko.artauction.auction.exception.AuctionNotFoundException
import ua.marchenko.artauction.auction.exception.InvalidAuctionOperationException
import ua.marchenko.artauction.auction.mapper.toAuction
import ua.marchenko.artauction.auction.mapper.toAuctionResponse

@Service
class AuctionServiceImpl(
    private val auctionRepository: AuctionRepository,
    private val artworkService: ArtworkService
) : AuctionService {

    override fun findAll() = auctionRepository.getAll()

    override fun findById(id: String) = auctionRepository.getByIdOrNull(id) ?: throwAuctionNotFoundException(id)

    override fun save(auction: AuctionRequest): AuctionResponse {
        val artwork = artworkService.findById(auction.artworkId)
        if (artwork.status != ArtworkStatus.VIEW) {
            throwInvalidAuctionOperationException("Trying to create auction with non-VIEW artwork")
        }
        val updatedArtwork = artworkService.update(
            auction.artworkId,
            artwork.copy(status = ArtworkStatus.ON_AUCTION),
            isStatusUpdated = true
        )
        return auctionRepository.save(auction.toAuction(updatedArtwork, null)).toAuctionResponse()
    }

    private fun throwAuctionNotFoundException(artworkId: String): Nothing = throw AuctionNotFoundException(artworkId)

    private fun throwInvalidAuctionOperationException(message: String): Nothing =
        throw InvalidAuctionOperationException(message)

}
