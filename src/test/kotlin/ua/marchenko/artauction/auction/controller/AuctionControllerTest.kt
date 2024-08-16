package ua.marchenko.artauction.auction.controller

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import ua.marchenko.artauction.auction.exception.AuctionNotFoundException
import ua.marchenko.artauction.auction.mapper.toAuctionResponse
import ua.marchenko.artauction.auction.service.AuctionService
import ua.marchenko.artauction.common.auction.getRandomAuction
import ua.marchenko.artauction.common.auction.getRandomAuctionRequest
import kotlin.test.Test

class AuctionControllerTest {

    private val mockAuctionService: AuctionService = mock(AuctionService::class.java)
    private val auctionController: AuctionController = AuctionController(mockAuctionService)

    @Test
    fun `getAllAuctions should return a list of AuctionResponse`() {
        val auctions = listOf(getRandomAuction())
        `when`(mockAuctionService.findAll()).thenReturn(auctions)
        val result = auctionController.getAllAuctions()
        assertEquals(1, result.size)
        assertEquals(auctions[0].toAuctionResponse(), result[0])
    }

    @Test
    fun `getAllAuctions should return an empty list if there are no auction`() {
        `when`(mockAuctionService.findAll()).thenReturn(listOf())
        val result = auctionController.getAllAuctions()
        assertEquals(0, result.size)
    }

    @Test
    fun `getAuctionById should return auction with given id if auction with this id exists`() {
        val id = "1"
        val auction = getRandomAuction(id = id)
        `when`(mockAuctionService.findById(id)).thenReturn(auction)
        val result = auctionController.getAuctionById(id)
        assertEquals(auction.toAuctionResponse(), result)
    }

    @Test
    fun `getAuctionById should throw AuctionNotFoundException if there is no auction with this id`() {
        val id = "1"
        `when`(mockAuctionService.findById(id)).thenThrow(AuctionNotFoundException(id))
        assertThrows<AuctionNotFoundException> { auctionController.getAuctionById(id) }
    }

    @Test
    fun `save should return AuctionResponse`() {
        val auctionRequest = getRandomAuctionRequest()
        val auction = getRandomAuction()
        `when`(mockAuctionService.save(auctionRequest)).thenReturn(auction.toAuctionResponse())
        val result = auctionController.addAuction(auctionRequest)
        assertEquals(auction.toAuctionResponse(), result)
    }

}
