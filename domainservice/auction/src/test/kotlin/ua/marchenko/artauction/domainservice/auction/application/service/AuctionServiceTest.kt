package ua.marchenko.artauction.domainservice.auction.application.service

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlin.test.Test
import org.bson.types.ObjectId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test
import reactor.kotlin.test.verifyError
import ua.marchenko.artauction.domainservice.auction.application.port.output.AuctionCreatedEventProducerOutputPort
import ua.marchenko.artauction.domainservice.auction.application.port.output.AuctionRepositoryOutputPort
import ua.marchenko.artauction.domainservice.auction.domain.Auction
import ua.marchenko.artauction.domainservice.auction.domain.projection.AuctionFull
import ua.marchenko.artauction.domainservice.auction.domain.random
import ua.marchenko.artauction.domainservice.auction.getRandomString
import ua.marchenko.artauction.core.auction.exception.AuctionNotFoundException
import ua.marchenko.artauction.core.auction.exception.InvalidAuctionOperationException
import ua.marchenko.artauction.domainservice.artwork.application.port.input.ArtworkServiceInputPort
import ua.marchenko.artauction.domainservice.artwork.domain.Artwork
import ua.marchenko.artauction.domainservice.artwork.domain.random
import ua.marchenko.artauction.domainservice.artwork.domain.Artwork.ArtworkStatus

class AuctionServiceTest {

    @MockK
    private lateinit var mockAuctionRepository: AuctionRepositoryOutputPort

    @MockK
    private lateinit var mockArtworkService: ArtworkServiceInputPort

    @MockK
    private lateinit var mockAuctionEventKafkaProducer: AuctionCreatedEventProducerOutputPort

    @InjectMockKs
    private lateinit var auctionService: AuctionService

    @Test
    fun `should return a list of auctions when auctions are present`() {
        // GIVEN
        val auctions = listOf(Auction.random())
        every { mockAuctionRepository.findAll() } returns auctions.toFlux()

        // WHEN
        val result = auctionService.getAll()

        // THEN
        result.test()
            .expectNext(auctions[0])
            .verifyComplete()
    }

    @Test
    fun `should return an empty list of auctions when there are no auctions`() {
        // GIVEN
        every { mockAuctionRepository.findAll() } returns Flux.empty()

        // WHEN
        val result = auctionService.getAll()

        // THEN
        result.test()
            .verifyComplete()
    }

    @Test
    fun `should return auction by id when auction with this id exists`() {
        // GIVEN
        val id = ObjectId().toHexString()
        val auction = Auction.random(id = id)

        every { mockAuctionRepository.findById(id) } returns auction.toMono()

        // WHEN
        val result = auctionService.getById(id)

        // THEN
        result.test()
            .expectNext(auction)
            .verifyComplete()
    }

    @Test
    fun `should throw AuctionNotFoundException when there is no artwork with this id`() {
        // GIVEN
        every { mockAuctionRepository.findById(any()) } returns Mono.empty()

        // WHEN
        val result = auctionService.getById(ObjectId().toHexString())

        // THEN
        result.test()
            .verifyError(AuctionNotFoundException::class)
    }

    @Test
    fun `should return full auction by id when auction with this id exists`() {
        // GIVEN
        val id = ObjectId().toHexString()
        val auction = AuctionFull.random(id = id)

        every { mockAuctionRepository.findFullById(id) } returns auction.toMono()

        // WHEN
        val result = auctionService.getFullById(id)

        // THEN
        result.test()
            .expectNext(auction)
            .verifyComplete()
    }

    @Test
    fun `should throw AuctionNotFoundException when there is no full auction with this id`() {
        // GIVEN
        every { mockAuctionRepository.findFullById(any()) } returns Mono.empty()

        // WHEN
        val result = auctionService.getFullById(ObjectId().toHexString())

        // THEN
        result.test()
            .verifyError(AuctionNotFoundException::class)
    }

    @Test
    fun `should return a list of full auctions when auctions are present`() {
        // GIVEN
        val auctions = listOf(AuctionFull.random())
        every { mockAuctionRepository.findFullAll() } returns auctions.toFlux()

        // WHEN
        val result = auctionService.getFullAll()

        // THEN
        result.test()
            .expectNext(auctions[0])
            .verifyComplete()
    }

    @Test
    fun `should change artwork status and save when artwork exist and artwork status is view`() {
        //GIVEN
        val newAuctionId = getRandomString()
        val artwork = Artwork.random(status = ArtworkStatus.ON_AUCTION)
        val auction = Auction.random(artworkId = artwork.id!!)

        every {
            mockArtworkService.updateStatusByIdAndPreviousStatus(
                auction.artworkId,
                ArtworkStatus.VIEW,
                ArtworkStatus.ON_AUCTION
            )
        } returns artwork.toMono()
        every { mockAuctionRepository.save(auction) } returns auction.copy(id = newAuctionId).toMono()
        every {
            mockAuctionEventKafkaProducer.sendCreateAuctionEvent(auction.copy(id = newAuctionId))
        } returns Unit.toMono()

        //WHEN
        val result = auctionService.save(auction)

        //THEN
        result.test()
            .expectNext(auction.copy(id = newAuctionId))
            .verifyComplete()
        verify(exactly = 1) { mockAuctionEventKafkaProducer.sendCreateAuctionEvent(auction.copy(id = newAuctionId)) }
    }

    @Test
    fun `should throw InvalidAuctionOperationException when artwork doesnt have VIEW status`() {
        // GIVEN
        val auctionRequest = Auction.random(artworkId = ObjectId().toHexString())

        every {
            mockArtworkService.updateStatusByIdAndPreviousStatus(
                auctionRequest.artworkId,
                ArtworkStatus.VIEW,
                ArtworkStatus.ON_AUCTION
            )
        } returns Mono.empty()

        // WHEN
        val result = auctionService.save(auctionRequest)

        // THEN
        result.test()
            .verifyError(InvalidAuctionOperationException::class)
        verify(exactly = 0) { mockAuctionEventKafkaProducer.sendCreateAuctionEvent(any()) }
    }
}
