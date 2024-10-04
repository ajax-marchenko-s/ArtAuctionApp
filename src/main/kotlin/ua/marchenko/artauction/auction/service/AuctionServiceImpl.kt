package ua.marchenko.artauction.auction.service

import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ua.marchenko.artauction.artwork.enums.ArtworkStatus
import ua.marchenko.artauction.auction.controller.dto.CreateAuctionRequest
import ua.marchenko.artauction.auction.repository.AuctionRepository
import ua.marchenko.artauction.artwork.service.ArtworkService
import ua.marchenko.artauction.auction.exception.AuctionNotFoundException
import ua.marchenko.artauction.auction.exception.InvalidAuctionOperationException
import ua.marchenko.artauction.auction.mapper.toMongo
import ua.marchenko.artauction.auction.model.MongoAuction
import ua.marchenko.artauction.auction.model.projection.AuctionFull

@Service
class AuctionServiceImpl(
    private val auctionRepository: AuctionRepository,
    private val artworkService: ArtworkService,
) : AuctionService {

    override fun getAll(page: Int, limit: Int): Flux<MongoAuction> = auctionRepository.findAll(page, limit)

    override fun getFullAll(page: Int, limit: Int): Flux<AuctionFull> = auctionRepository.findFullAll(page, limit)

    override fun getById(id: String): Mono<MongoAuction> =
        auctionRepository.findById(id).switchIfEmpty(Mono.error(AuctionNotFoundException(id)))

    override fun getFullById(id: String): Mono<AuctionFull> =
        auctionRepository.findFullById(id).switchIfEmpty(Mono.error(AuctionNotFoundException(id)))

    override fun save(auction: CreateAuctionRequest): Mono<MongoAuction> {
        return artworkService.getById(auction.artworkId).flatMap { artwork ->
            if (artwork.status != ArtworkStatus.VIEW) {
                Mono.error(InvalidAuctionOperationException("Trying to create auction with non-VIEW artwork"))
            } else {
                artworkService.updateStatus(auction.artworkId, ArtworkStatus.ON_AUCTION)
                    .then(auctionRepository.save(auction.toMongo()))
            }
        }
    }
}
