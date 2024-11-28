package ua.marchenko.artauction.domainservice.auction.application.service

import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import ua.marchenko.artauction.domainservice.auction.application.port.input.AuctionServiceInputPort
import ua.marchenko.artauction.domainservice.auction.application.port.output.AuctionCreatedEventProducerOutputPort
import ua.marchenko.artauction.domainservice.auction.application.port.output.AuctionRepositoryOutputPort
import ua.marchenko.artauction.domainservice.auction.domain.Auction
import ua.marchenko.artauction.domainservice.auction.domain.projection.AuctionFull
import ua.marchenko.artauction.core.auction.exception.AuctionNotFoundException
import ua.marchenko.artauction.core.auction.exception.InvalidAuctionOperationException
import ua.marchenko.artauction.domainservice.artwork.application.port.input.ArtworkServiceInputPort
import ua.marchenko.artauction.domainservice.artwork.domain.Artwork.ArtworkStatus

@Service
class AuctionService(
    private val auctionRepository: AuctionRepositoryOutputPort,
    private val artworkService: ArtworkServiceInputPort,
    private val auctionEventKafkaProducer: AuctionCreatedEventProducerOutputPort,
) : AuctionServiceInputPort {
    override fun getAll(page: Int, limit: Int): Flux<Auction> = auctionRepository.findAll(page, limit)

    override fun getFullAll(page: Int, limit: Int): Flux<AuctionFull> = auctionRepository.findFullAll(page, limit)

    override fun getById(id: String): Mono<Auction> =
        auctionRepository.findById(id).switchIfEmpty { Mono.error(AuctionNotFoundException(id)) }

    override fun getFullById(id: String): Mono<AuctionFull> =
        auctionRepository.findFullById(id).switchIfEmpty { Mono.error(AuctionNotFoundException(id)) }

    override fun save(auction: Auction): Mono<Auction> {
        return artworkService.updateStatusByIdAndPreviousStatus(
            auction.artworkId,
            ArtworkStatus.VIEW,
            ArtworkStatus.ON_AUCTION
        )
            .switchIfEmpty {
                Mono.error(InvalidAuctionOperationException("Cannot create auction: Artwork is not in VIEW status"))
            }
            .flatMap { auctionRepository.save(auction) }
            .flatMap { savedAuction ->
                auctionEventKafkaProducer.sendCreateAuctionEvent(savedAuction)
                    .then(savedAuction.toMono())
                    .onErrorReturn(savedAuction)
            }
    }
}
