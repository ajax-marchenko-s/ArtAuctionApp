//package ua.marchenko.artauction.auction.service
//
//import org.junit.jupiter.api.Assertions.assertEquals
//import org.junit.jupiter.api.assertThrows
//import ua.marchenko.artauction.artwork.enums.ArtworkStatus
//import ua.marchenko.artauction.artwork.service.ArtworkService
//import ua.marchenko.artauction.auction.exception.AuctionNotFoundException
//import ua.marchenko.artauction.auction.exception.InvalidAuctionOperationException
//import ua.marchenko.artauction.auction.repository.AuctionRepository
//import artwork.random
//import auction.random
//import io.mockk.every
//import io.mockk.impl.annotations.InjectMockKs
//import io.mockk.impl.annotations.MockK
//import kotlin.test.Test
//import org.bson.types.ObjectId
//import ua.marchenko.artauction.artwork.model.MongoArtwork
//import ua.marchenko.artauction.auction.controller.dto.CreateAuctionRequest
//import ua.marchenko.artauction.auction.mapper.toMongo
//import ua.marchenko.artauction.auction.mapper.toResponse
//import ua.marchenko.artauction.auction.model.MongoAuction
//import ua.marchenko.artauction.auction.model.projection.AuctionFull
//
//class AuctionServiceTest {
//
//    @MockK
//    private lateinit var mockAuctionRepository: AuctionRepository
//
//    @MockK
//    private lateinit var mockArtworkService: ArtworkService
//
//    @InjectMockKs
//    private lateinit var auctionService: AuctionServiceImpl
//
//    @Test
//    fun `should return a list of auctions when auctions are present`() {
//        //GIVEN
//        val auctions = listOf(MongoAuction.random())
//        every { mockAuctionRepository.findAll() } returns auctions
//
//        //WHEN
//        val result = auctionService.getAll()
//
//        //THEN
//        assertEquals(1, result.size)
//        assertEquals(auctions[0].id, result[0].id)
//    }
//
//    @Test
//    fun `should return an empty list of auctions when there are no auctions`() {
//        //GIVEN
//        every { mockAuctionRepository.findAll() } returns emptyList()
//
//        //WHEN
//        val result = auctionService.getAll()
//
//        //THEN
//        assertEquals(0, result.size)
//    }
//
//    @Test
//    fun `should return auction by id when auction with this id exists`() {
//        //GIVEN
//        val id = ObjectId().toHexString()
//        val auction = MongoAuction.random(id = id)
//
//        every { mockAuctionRepository.findById(id) } returns auction
//
//        //WHEN
//        val result = auctionService.getById(id)
//
//        //THEN
//        assertEquals(auction, result)
//    }
//
//    @Test
//    fun `should throw AuctionNotFoundException when there is no artwork with this id`() {
//        //GIVEN
//        every { mockAuctionRepository.findById(any()) } returns null
//
//        //WHEN //THEN
//        assertThrows<AuctionNotFoundException> { auctionService.getById(ObjectId().toHexString()) }
//    }
//
//    @Test
//    fun `should return full auction by id when auction with this id exists`() {
//        // GIVEN
//        val id = ObjectId().toHexString()
//        val auction = AuctionFull.random(id = id)
//
//        every { mockAuctionRepository.findFullById(id) } returns auction
//
//        //WHEN
//        val result = auctionService.getFullById(id)
//
//        //THEN
//        assertEquals(auction, result)
//    }
//
//    @Test
//    fun `should throw AuctionNotFoundException when there is no full auction with this id`() {
//        //GIVEN
//        every { mockAuctionRepository.findFullById(any()) } returns null
//
//        //WHEN //THEN
//        assertThrows<AuctionNotFoundException> { auctionService.getFullById(ObjectId().toHexString()) }
//    }
//
//    @Test
//    fun `should return a list of full auctions when auctions are present`() {
//        // GIVEN
//        val auctions = listOf(AuctionFull.random())
//        every { mockAuctionRepository.findFullAll() } returns auctions
//
//        //WHEN
//        val result = auctionService.getFullAll()
//
//        //THEN
//        assertEquals(1, result.size)
//        assertEquals(auctions[0].artwork, result[0].artwork)
//    }
//
//    @Test
//    fun `should change artwork status and save when artwork exist and artwork status is view`() {
//        //GIVEN
//        val newAuctionId = ObjectId()
//        val artwork = MongoArtwork.random(status = ArtworkStatus.VIEW)
//        val auctionRequest = CreateAuctionRequest.random(artworkId = artwork.id!!.toHexString())
//        val auction = auctionRequest.toMongo()
//
//        every { mockArtworkService.getById(auctionRequest.artworkId) } returns artwork
//        every {
//            mockArtworkService.updateStatus(
//                auctionRequest.artworkId,
//                ArtworkStatus.ON_AUCTION
//            )
//        } returns artwork.copy(status = ArtworkStatus.ON_AUCTION)
//        every { mockAuctionRepository.save(auction) } returns auction.copy(id = newAuctionId)
//
//        //WHEN
//        val result = auctionService.save(auctionRequest)
//
//        //THEN
//        assertEquals(auction.copy(id = newAuctionId).toResponse(), result)
//    }
//
//    @Test
//    fun `should throw InvalidAuctionOperationException when artwork doesnt have VIEW status`() {
//        //GIVEN
//        val artwork = MongoArtwork.random(status = ArtworkStatus.ON_AUCTION)
//        val auctionRequest = CreateAuctionRequest.random(artworkId = artwork.id!!.toHexString())
//
//        every { mockArtworkService.getById(auctionRequest.artworkId) } returns artwork
//
//        //WHEN //THEN
//        assertThrows<InvalidAuctionOperationException> { auctionService.save(auctionRequest) }
//    }
//}
