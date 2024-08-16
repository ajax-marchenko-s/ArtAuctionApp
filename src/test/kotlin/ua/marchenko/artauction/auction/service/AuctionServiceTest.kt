package ua.marchenko.artauction.auction.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import ua.marchenko.artauction.artwork.service.ArtworkService
import ua.marchenko.artauction.auction.exception.AuctionNotFoundException
import ua.marchenko.artauction.auction.model.Auction
import ua.marchenko.artauction.auction.repository.AuctionRepository
import ua.marchenko.artauction.common.auction.getRandomAuction
import kotlin.test.Test

class AuctionServiceTest {

    private val mockAuctionRepository = mock(AuctionRepository::class.java)
    private val mockArtworkService: ArtworkService = mock(ArtworkService::class.java)
    private val auctionService: AuctionService = AuctionServiceImpl(mockAuctionRepository, mockArtworkService)

    @Test
    fun `findAll should return a list of auctions if there are present`() {
        val auctions = listOf(getRandomAuction())
        `when`(mockAuctionRepository.getAll()).thenReturn(auctions)
        val result = auctionService.findAll()
        assertEquals(1, result.size)
        assertEquals(auctions[0].id, result[0].id)
    }

    @Test
    fun `findAll should return an empty list of auctions if there are no auctions`() {
        `when`(mockAuctionRepository.getAll()).thenReturn(listOf<Auction>())
        val result = auctionService.findAll()
        assertEquals(0, result.size)
    }

    @Test
    fun `findById should return auction by id if auction with this id exists`() {
        val id = "1"
        val auction = getRandomAuction(id)
        `when`(mockAuctionRepository.getByIdOrNull(id)).thenReturn(auction)
        val result = auctionService.findById(id)
        assertEquals(auction, result)
    }

    @Test
    fun `findById should throw AuctionNotFoundException if there is no artwork with this id`() {
        val id = "1"
        `when`(mockAuctionRepository.getByIdOrNull(id)).thenReturn(null)
        assertThrows<AuctionNotFoundException> { auctionService.findById(id) }
    }

    //todo add test for save method

}