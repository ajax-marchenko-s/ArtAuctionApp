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
import artwork.random
import auction.random
import getRandomObjectId
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import ua.marchenko.artauction.artwork.model.Artwork
import ua.marchenko.artauction.auction.controller.dto.CreateAuctionRequest
import ua.marchenko.artauction.auction.model.Auction


class AuctionServiceTest {

    @MockK
    private lateinit var mockAuctionRepository: AuctionRepository

    @MockK
    private lateinit var mockArtworkService: ArtworkService

    @InjectMockKs
    private lateinit var auctionService: AuctionServiceImpl

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `should return a list of auctions when auctions are present`() {
        //GIVEN
        val auctions = listOf(Auction.random())
        every { mockAuctionRepository.findAll() } returns (auctions)

        //WHEN
        val result = auctionService.getAll()

        //THEN
        assertEquals(1, result.size)
        assertEquals(auctions[0].id, result[0].id)
    }

    @Test
    fun `should return an empty list of auctions when there are no auctions`() {
        //GIVEN
        every { mockAuctionRepository.findAll() } returns (listOf())

        //WHEN
        val result = auctionService.getAll()

        //THEN
        assertEquals(0, result.size)
    }

    @Test
    fun `should return auction by id when auction with this id exists`() {
        //GIVEN
        val id = getRandomObjectId().toString()
        val auction = Auction.random(id = id)

        every { mockAuctionRepository.findById(id) } returns (auction)

        //WHEN
        val result = auctionService.getById(id)

        //THEN
        assertEquals(auction, result)
    }

    @Test
    fun `should throw AuctionNotFoundException when there is no artwork with this id`() {
        //GIVEN
        val id = getRandomObjectId().toString()
        every { mockAuctionRepository.findById(id) } returns (null)

        //WHEN //THEN
        assertThrows<AuctionNotFoundException> { auctionService.getById(id) }
    }

    @Test
    fun `should change artwork status and save when artwork exist and artwork status is view`() {
        //GIVEN
        val newAuctionId = getRandomObjectId()
        val artwork = Artwork.random(status = ArtworkStatus.VIEW)
        val auctionRequest = CreateAuctionRequest.random(artworkId = artwork.id!!.toString())
        val auction = auctionRequest.toAuction(artwork.copy(status = ArtworkStatus.ON_AUCTION), null)

        every { mockArtworkService.getById(auctionRequest.artworkId) } returns (artwork)
        every {
            mockArtworkService.updateStatus(
                auctionRequest.artworkId,
                ArtworkStatus.ON_AUCTION
            )
        } returns (artwork.copy(status = ArtworkStatus.ON_AUCTION))
        every { mockAuctionRepository.save(auction) } returns (auction.copy(id = newAuctionId))

        //WHEN
        val result = auctionService.save(auctionRequest)

        //THEN
        assertEquals(auction.copy(id = newAuctionId).toAuctionResponse(), result)
    }

    @Test
    fun `should throw InvalidAuctionOperationException when artwork doesnt have VIEW status`() {
        //GIVEN
        val artwork = Artwork.random(status = ArtworkStatus.ON_AUCTION)
        val auctionRequest = CreateAuctionRequest.random(artworkId = artwork.id!!.toString())

        every { mockArtworkService.getById(auctionRequest.artworkId) } returns (artwork)

        //WHEN //THEN
        assertThrows<InvalidAuctionOperationException> { auctionService.save(auctionRequest) }
    }
}
