package ua.marchenko.artauction.auction.controller

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import ua.marchenko.artauction.auction.exception.AuctionNotFoundException
import ua.marchenko.artauction.auction.mapper.toAuctionResponse
import ua.marchenko.artauction.auction.service.AuctionService
import auction.getRandomAuction
import auction.getRandomAuctionRequest
import getRandomObjectId
import kotlin.test.Test
import getRandomString

class AuctionControllerTest {

    private val mockAuctionService: AuctionService = mock(AuctionService::class.java)
    private val auctionController: AuctionController = AuctionController(mockAuctionService)

    @Test
    fun `getAllAuctions should return a list of AuctionResponse`() {
        //GIVEN
        val auctions = listOf(getRandomAuction())
        `when`(mockAuctionService.getAll()).thenReturn(auctions)

        //WHEN
        val result = auctionController.getAllAuctions()

        //THEN
        assertEquals(1, result.size)
        assertEquals(auctions[0].toAuctionResponse(), result[0])
    }

    @Test
    fun `getAllAuctions should return an empty list if there are no auction`() {
        //GIVEN
        `when`(mockAuctionService.getAll()).thenReturn(listOf())

        //WHEN
        val result = auctionController.getAllAuctions()

        //THEN
        assertEquals(0, result.size)
    }

    @Test
    fun `getAuctionById should return auction with given id if auction with this id exists`() {
        //GIVEN
        val id = getRandomObjectId().toString()
        val auction = getRandomAuction(id = id)

        `when`(mockAuctionService.getById(id)).thenReturn(auction)

        //WHEN
        val result = auctionController.getAuctionById(id)

        //THEN
        assertEquals(auction.toAuctionResponse(), result)
    }

    @Test
    fun `getAuctionById should throw AuctionNotFoundException if there is no auction with this id`() {
        //GIVEN
        val id = getRandomString()
        `when`(mockAuctionService.getById(id)).thenThrow(AuctionNotFoundException(id))

        //WHEN-THEN
        assertThrows<AuctionNotFoundException> { auctionController.getAuctionById(id) }
    }

    @Test
    fun `save should return AuctionResponse`() {
        //GIVEN
        val auctionRequest = getRandomAuctionRequest()
        val auction = getRandomAuction()

        `when`(mockAuctionService.save(auctionRequest)).thenReturn(auction.toAuctionResponse())

        //WHEN
        val result = auctionController.addAuction(auctionRequest)

        //THEN
        assertEquals(auction.toAuctionResponse(), result)
    }
}
