package ua.marchenko.artauction.auction.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import ua.marchenko.artauction.artwork.enums.ArtworkStatus
import ua.marchenko.artauction.artwork.service.ArtworkService
import ua.marchenko.artauction.auction.exception.AuctionNotFoundException
import ua.marchenko.artauction.auction.exception.InvalidAuctionOperationException
import ua.marchenko.artauction.auction.mapper.toAuction
import ua.marchenko.artauction.auction.mapper.toAuctionResponse
import ua.marchenko.artauction.auction.model.Auction
import ua.marchenko.artauction.auction.repository.AuctionRepository
import ua.marchenko.artauction.common.artwork.getRandomArtwork
import ua.marchenko.artauction.common.auction.getRandomAuction
import ua.marchenko.artauction.common.auction.getRandomAuctionRequest
import ua.marchenko.artauction.common.getRandomString
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

    @Test
    fun `save should change artwork status and save`() {
        val newAuctionId = getRandomString()
        val artwork = getRandomArtwork()
        val auctionRequest = getRandomAuctionRequest(artworkId = artwork.id ?: "")
        val auction = auctionRequest.toAuction(artwork.copy(status = ArtworkStatus.ON_AUCTION), null)
        `when`(mockArtworkService.findById(auctionRequest.artworkId)).thenReturn(artwork)
        `when`(
            mockArtworkService.update(
                auctionRequest.artworkId,
                artwork.copy(status = ArtworkStatus.ON_AUCTION),
                true
            )
        ).thenReturn(artwork.copy(status = ArtworkStatus.ON_AUCTION))
        `when`(mockAuctionRepository.save(auction)).thenReturn(auction.copy(id = newAuctionId))
        val result = auctionService.save(auctionRequest)
        assertEquals(auction.copy(id = newAuctionId).toAuctionResponse(), result)
    }

    @Test
    fun `save should throw InvalidAuctionOperationException if artwork doesnt have VIEW status`() {
        val artwork = getRandomArtwork(status = ArtworkStatus.ON_AUCTION)
        val auctionRequest = getRandomAuctionRequest(artworkId = artwork.id ?: "")
        `when`(mockArtworkService.findById(auctionRequest.artworkId)).thenReturn(artwork)
        assertThrows<InvalidAuctionOperationException> { auctionService.save(auctionRequest) }
    }

}