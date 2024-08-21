package ua.marchenko.artauction.auction.controller

import auction.random
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import ua.marchenko.artauction.auction.exception.AuctionNotFoundException
import ua.marchenko.artauction.auction.mapper.toAuctionResponse
import ua.marchenko.artauction.auction.service.AuctionService
import getRandomObjectId
import kotlin.test.Test
import getRandomString
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.whenever
import ua.marchenko.artauction.auction.controller.dto.CreateAuctionRequest
import ua.marchenko.artauction.auction.model.Auction

class AuctionControllerTest {

    private val mockAuctionService: AuctionService = mock(AuctionService::class.java)
    private val auctionController: AuctionController = AuctionController(mockAuctionService)

    @Test
    fun `getAllAuctions should return a list of AuctionResponse`() {
        //GIVEN
        val auctions = listOf(Auction.random())
        whenever(mockAuctionService.getAll()) doReturn (auctions)

        //WHEN
        val result = auctionController.getAllAuctions()

        //THEN
        assertEquals(1, result.size)
        assertEquals(auctions[0].toAuctionResponse(), result[0])
    }

    @Test
    fun `getAllAuctions should return an empty list if there are no auction`() {
        //GIVEN
        whenever(mockAuctionService.getAll()) doReturn (listOf())

        //WHEN
        val result = auctionController.getAllAuctions()

        //THEN
        assertEquals(0, result.size)
    }

    @Test
    fun `getAuctionById should return auction with given id if auction with this id exists`() {
        //GIVEN
        val id = getRandomObjectId().toString()
        val auction = Auction.random(id = id)

        whenever(mockAuctionService.getById(id)) doReturn (auction)

        //WHEN
        val result = auctionController.getAuctionById(id)

        //THEN
        assertEquals(auction.toAuctionResponse(), result)
    }

    @Test
    fun `getAuctionById should throw AuctionNotFoundException if there is no auction with this id`() {
        //GIVEN
        val id = getRandomString()
        whenever(mockAuctionService.getById(id)) doThrow (AuctionNotFoundException(id))

        //WHEN-THEN
        assertThrows<AuctionNotFoundException> { auctionController.getAuctionById(id) }
    }

    @Test
    fun `save should return AuctionResponse`() {
        //GIVEN
        val auctionRequest = CreateAuctionRequest.random()
        val auction = Auction.random()

        whenever(mockAuctionService.save(auctionRequest)) doReturn (auction.toAuctionResponse())

        //WHEN
        val result = auctionController.addAuction(auctionRequest)

        //THEN
        assertEquals(auction.toAuctionResponse(), result)
    }
}
