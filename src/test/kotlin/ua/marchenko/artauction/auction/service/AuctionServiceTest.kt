package ua.marchenko.artauction.auction.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import ua.marchenko.artauction.artwork.enums.ArtworkStatus
import ua.marchenko.artauction.artwork.service.ArtworkService
import ua.marchenko.artauction.auction.exception.AuctionNotFoundException
import ua.marchenko.artauction.auction.exception.InvalidAuctionOperationException
import ua.marchenko.artauction.auction.mapper.toAuction
import ua.marchenko.artauction.auction.mapper.toAuctionResponse
import ua.marchenko.artauction.auction.repository.AuctionRepository
import ua.marchenko.artauction.common.artwork.getRandomArtwork
import ua.marchenko.artauction.common.auction.getRandomAuction
import ua.marchenko.artauction.common.auction.getRandomAuctionRequest
import ua.marchenko.artauction.common.getRandomString
import kotlin.test.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class AuctionServiceTest {

    private val mockAuctionRepository = mock(AuctionRepository::class.java)
    private val mockArtworkService: ArtworkService = mock(ArtworkService::class.java)
    private val auctionService: AuctionService = AuctionServiceImpl(mockAuctionRepository, mockArtworkService)

    @Test
    fun `getAll should return a list of auctions if there are present`() {
        //GIVEN
        val auctions = listOf(getRandomAuction())
        `when`(mockAuctionRepository.findAll()).thenReturn(auctions)

        //WHEN
        val result = auctionService.getAll()

        //THEN
        assertEquals(1, result.size)
        assertEquals(auctions[0].id, result[0].id)
    }

    @Test
    fun `getAll should return an empty list of auctions if there are no auctions`() {
        //GIVEN
        `when`(mockAuctionRepository.findAll()).thenReturn(listOf())

        //WHEN
        val result = auctionService.getAll()

        //THEN
        assertEquals(0, result.size)
    }

    @Test
    fun `getById should return auction by id if auction with this id exists`() {
        //GIVEN
        val id = getRandomString()
        val auction = getRandomAuction(id)

        `when`(mockAuctionRepository.findById(id)).thenReturn(auction)

        //WHEN
        val result = auctionService.getById(id)

        //THEN
        assertEquals(auction, result)
    }

    @Test
    fun `getById should throw AuctionNotFoundException if there is no artwork with this id`() {
        //GIVEN
        val id = getRandomString()
        `when`(mockAuctionRepository.findById(id)).thenReturn(null)

        //WHEN-THEN
        assertThrows<AuctionNotFoundException> { auctionService.getById(id) }
    }

    @Test
    fun `save should change artwork status and save`() {
        //GIVEN
        val newAuctionId = getRandomString()
        val artwork = getRandomArtwork()
        val auctionRequest = getRandomAuctionRequest(artworkId = artwork.id!!)
        val auction = auctionRequest.toAuction(artwork.copy(status = ArtworkStatus.ON_AUCTION), null)

        `when`(mockArtworkService.getById(auctionRequest.artworkId)).thenReturn(artwork)
        `when`(
            mockArtworkService.updateStatus(
                auctionRequest.artworkId,
                ArtworkStatus.ON_AUCTION
            )
        ).thenReturn(artwork.copy(status = ArtworkStatus.ON_AUCTION))
        `when`(mockAuctionRepository.save(auction)).thenReturn(auction.copy(id = newAuctionId))

        //WHEN
        val result = auctionService.save(auctionRequest)

        //THEN
        assertEquals(auction.copy(id = newAuctionId).toAuctionResponse(), result)
    }

    @Test
    fun `save should throw InvalidAuctionOperationException if artwork doesnt have VIEW status`() {
        //GIVEN
        val artwork = getRandomArtwork(status = ArtworkStatus.ON_AUCTION)
        val auctionRequest = getRandomAuctionRequest(artworkId = artwork.id!!)

        `when`(mockArtworkService.getById(auctionRequest.artworkId)).thenReturn(artwork)

        //WHEN-THEN
        assertThrows<InvalidAuctionOperationException> { auctionService.save(auctionRequest) }
    }
}
