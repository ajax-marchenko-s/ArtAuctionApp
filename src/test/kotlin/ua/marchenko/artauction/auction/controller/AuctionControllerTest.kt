package ua.marchenko.artauction.auction.controller

import auction.random
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import ua.marchenko.artauction.auction.exception.AuctionNotFoundException
import ua.marchenko.artauction.auction.mapper.toAuctionResponse
import ua.marchenko.artauction.auction.service.AuctionService
import getRandomObjectId
import kotlin.test.Test
import getRandomString
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import ua.marchenko.artauction.auction.controller.dto.CreateAuctionRequest
import ua.marchenko.artauction.auction.model.Auction

@ExtendWith(MockKExtension::class)
class AuctionControllerTest {

    @MockK
    lateinit var mockAuctionService: AuctionService

    @InjectMockKs
    private lateinit var auctionController: AuctionController

    @Test
    fun `should return a list of AuctionResponse when there are some auctions`() {
        //GIVEN
        val auctions = listOf(Auction.random())
        every { mockAuctionService.getAll() } returns auctions

        //WHEN
        val result = auctionController.getAllAuctions()

        //THEN
        assertEquals(1, result.size)
        assertEquals(auctions[0].toAuctionResponse(), result[0])
    }

    @Test
    fun `should return an empty list when there are no auction`() {
        //GIVEN
        every { mockAuctionService.getAll() } returns emptyList()

        //WHEN
        val result = auctionController.getAllAuctions()

        //THEN
        assertEquals(0, result.size)
    }

    @Test
    fun `should return auction with given id when auction with this id exists`() {
        //GIVEN
        val id = getRandomObjectId().toHexString()
        val auction = Auction.random(id = id)

        every { mockAuctionService.getById(id) } returns auction

        //WHEN
        val result = auctionController.getAuctionById(id)

        //THEN
        assertEquals(auction.toAuctionResponse(), result)
    }

    @Test
    fun `should throw AuctionNotFoundException when there is no auction with this id`() {
        //GIVEN
        val id = getRandomString()
        every { mockAuctionService.getById(id) } throws AuctionNotFoundException(id)

        //WHEN //THEN
        assertThrows<AuctionNotFoundException> { auctionController.getAuctionById(id) }
    }

    @Test
    fun `should return AuctionResponse`() {
        //GIVEN
        val auctionRequest = CreateAuctionRequest.random()
        val auction = Auction.random()

        every { mockAuctionService.save(auctionRequest) } returns auction.toAuctionResponse()

        //WHEN
        val result = auctionController.addAuction(auctionRequest)

        //THEN
        assertEquals(auction.toAuctionResponse(), result)
    }
}
