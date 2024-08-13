package ua.marchenko.artauction.service.auction

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import ua.marchenko.artauction.dto.auction.AuctionRequest
import ua.marchenko.artauction.dto.auction.AuctionResponse
import ua.marchenko.artauction.exception.type.artwork.ArtworkNotFoundException
import ua.marchenko.artauction.exception.type.general.IncorrectRoleException
import ua.marchenko.artauction.mapper.artwork.toArtwork
import ua.marchenko.artauction.mapper.auction.toAuction
import ua.marchenko.artauction.mapper.auction.toAuctionResponse
import ua.marchenko.artauction.repository.auction.MongoAuctionRepository
import ua.marchenko.artauction.service.artwork.ArtworkService

@Service
class AuctionServiceImpl(
    private val mongoAuctionRepository: MongoAuctionRepository,
    private val artworkService: ArtworkService
) : AuctionService {

    override fun findAll(): List<AuctionResponse> {
        return mongoAuctionRepository.findAll().map { it.toAuctionResponse() }
    }

    override fun findById(id: String): AuctionResponse {
        return mongoAuctionRepository.findByIdOrNull(id)?.toAuctionResponse() ?: throwArtworkNotFoundException(id)
    }

    override fun save(auctionRequest: AuctionRequest): AuctionResponse {
        val artwork = artworkService.findById(auctionRequest.artworkId)
       //TODO Logic that there is no auctions with this artwork
        return mongoAuctionRepository.save(auctionRequest.toAuction(artwork.toArtwork())).toAuctionResponse()
    }

    private fun throwArtworkNotFoundException(artworkId: String): Nothing {
        throw ArtworkNotFoundException(artworkId)
    }

    private fun throwIncorrectRoleException(message: String): Nothing {
        throw IncorrectRoleException(message)
    }
}