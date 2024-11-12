package ua.marchenko.artauction.auction.service

import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ua.marchenko.artauction.auction.controller.dto.CreateAuctionRequest
import ua.marchenko.artauction.auction.repository.AuctionRepository
import ua.marchenko.artauction.auction.exception.AuctionNotFoundException
import ua.marchenko.artauction.auction.mapper.toMongo
import ua.marchenko.artauction.auction.model.MongoAuction
import ua.marchenko.artauction.auction.model.projection.AuctionFull
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import ua.marchenko.artauction.artwork.service.ArtworkService
import ua.marchenko.artauction.auction.exception.InvalidAuctionOperationException
import ua.marchenko.artauction.auction.service.kafka.AuctionCreatedEventKafkaProducer
import ua.marchenko.core.artwork.enums.ArtworkStatus

@Service
class AuctionServiceImpl(
    private val auctionRepository: AuctionRepository,
    private val artworkService: ArtworkService,
    private val auctionEventKafkaProducer: AuctionCreatedEventKafkaProducer,
) : AuctionService {

    override fun getAll(page: Int, limit: Int): Flux<MongoAuction> = auctionRepository.findAll(page, limit)

    override fun getFullAll(page: Int, limit: Int): Flux<AuctionFull> = auctionRepository.findFullAll(page, limit)

    override fun getById(id: String): Mono<MongoAuction> =
        auctionRepository.findById(id).switchIfEmpty { Mono.error(AuctionNotFoundException(id)) }

    override fun getFullById(id: String): Mono<AuctionFull> =
        auctionRepository.findFullById(id).switchIfEmpty { Mono.error(AuctionNotFoundException(id)) }

    override fun save(auction: CreateAuctionRequest): Mono<MongoAuction> {
        return artworkService.updateStatusByIdAndPreviousStatus(
            auction.artworkId,
            ArtworkStatus.VIEW,
            ArtworkStatus.ON_AUCTION
        )
            .switchIfEmpty {
                Mono.error(InvalidAuctionOperationException("Cannot create auction: Artwork is not in VIEW status"))
            }
            .flatMap { auctionRepository.save(auction.toMongo()) }
            .flatMap { savedAuction ->
                auctionEventKafkaProducer.sendCreateAuctionEvent(savedAuction)
                    .then(savedAuction.toMono())
                    .onErrorReturn(savedAuction)
            }
    }
}
