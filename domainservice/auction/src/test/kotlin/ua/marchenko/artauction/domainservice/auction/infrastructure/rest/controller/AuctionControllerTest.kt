package ua.marchenko.artauction.domainservice.auction.infrastructure.rest.controller

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import kotlin.test.Test
import org.bson.types.ObjectId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test
import ua.marchenko.artauction.domainservice.auction.application.port.input.AuctionServiceInputPort
import ua.marchenko.artauction.domainservice.auction.domain.Auction
import ua.marchenko.artauction.domainservice.auction.domain.random
import ua.marchenko.artauction.domainservice.auction.getRandomString
import ua.marchenko.artauction.domainservice.auction.infrastructure.random
import ua.marchenko.artauction.domainservice.auction.infrastructure.rest.dto.CreateAuctionRequest
import ua.marchenko.artauction.domainservice.auction.infrastructure.rest.mapper.toResponse
import ua.marchenko.artauction.core.auction.exception.AuctionNotFoundException
import ua.marchenko.artauction.domainservice.auction.infrastructure.rest.mapper.toDomainCreate

class AuctionControllerTest {

    @MockK
    private lateinit var mockAuctionService: AuctionServiceInputPort

    @InjectMockKs
    private lateinit var auctionController: AuctionController

    @Test
    fun `should return a list of AuctionResponse when there are some auctions`() {
        // GIVEN
        val auctions = listOf(Auction.random())
        every { mockAuctionService.getAll() } returns auctions.toFlux()

        // WHEN
        val result = auctionController.getAllAuctions(0, 10)

        // THEN
        result.test()
            .expectNext(auctions[0].toResponse())
            .verifyComplete()
    }

    @Test
    fun `should return an empty list when there are no auction`() {
        // GIVEN
        every { mockAuctionService.getAll() } returns Flux.empty()

        // WHEN
        val result = auctionController.getAllAuctions(0, 10)

        // THEN
        result.test()
            .verifyComplete()
    }

    @Test
    fun `should return auction with given id when auction with this id exists`() {
        // GIVEN
        val id = ObjectId().toHexString()
        val auction = Auction.random(id = id)

        every { mockAuctionService.getById(id) } returns auction.toMono()

        // WHEN
        val result = auctionController.getAuctionById(id)

        // THEN
        result.test()
            .expectNext(auction.toResponse())
            .verifyComplete()
    }

    @Test
    fun `should throw AuctionNotFoundException when there is no auction with this id`() {
        // GIVEN
        val id = getRandomString()
        every { mockAuctionService.getById(id) } returns Mono.error(AuctionNotFoundException(id))

        // WHEN
        val result = auctionController.getAuctionById(id)

        // THEN
        result.test()
            .verifyError(AuctionNotFoundException::class.java)
    }

    @Test
    fun `should return AuctionResponse when creating auction`() {
        // GIVEN
        val auctionRequest = CreateAuctionRequest.random()
        val auction = Auction.random()

        every { mockAuctionService.save(auctionRequest.toDomainCreate()) } returns auction.toMono()

        // WHEN
        val result = auctionController.addAuction(auctionRequest)

        // THEN
        result.test()
            .expectNext(auction.toResponse())
            .verifyComplete()
    }
}
