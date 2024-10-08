package ua.marchenko.artauction.auction.controller

import auction.random
import ua.marchenko.artauction.auction.exception.AuctionNotFoundException
import ua.marchenko.artauction.auction.service.AuctionService
import kotlin.test.Test
import getRandomString
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.bson.types.ObjectId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test
import ua.marchenko.artauction.auction.controller.dto.CreateAuctionRequest
import ua.marchenko.artauction.auction.mapper.toResponse
import ua.marchenko.artauction.auction.model.MongoAuction

class AuctionControllerTest {

    @MockK
    private lateinit var mockAuctionService: AuctionService

    @InjectMockKs
    private lateinit var auctionController: AuctionController

    @Test
    fun `should return a list of AuctionResponse when there are some auctions`() {
        // GIVEN
        val auctions = listOf(MongoAuction.random())
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
        val auction = MongoAuction.random(id = id)

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
    fun `should return AuctionResponse`() {
        // GIVEN
        val auctionRequest = CreateAuctionRequest.random()
        val auction = MongoAuction.random()

        every { mockAuctionService.save(auctionRequest) } returns auction.toMono()

        // WHEN
        val result = auctionController.addAuction(auctionRequest)

        // THEN
        result.test()
            .expectNext(auction.toResponse())
            .verifyComplete()
    }
}
