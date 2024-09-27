package ua.marchenko.artauction.auction.repository

import auction.random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.beans.factory.annotation.Autowired
import ua.marchenko.artauction.artwork.model.MongoArtwork
import ua.marchenko.artauction.artwork.repository.ArtworkRepository
import ua.marchenko.artauction.auction.model.MongoAuction
import ua.marchenko.artauction.common.AbstractBaseIntegrationTest
import ua.marchenko.artauction.user.enums.Role
import ua.marchenko.artauction.user.model.MongoUser
import ua.marchenko.artauction.user.repository.UserRepository
import user.random

class AuctionRepositoryTest : AbstractBaseIntegrationTest {

    @Autowired
    private lateinit var auctionRepository: AuctionRepository

    @Autowired
    private lateinit var artworkRepository: ArtworkRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `should save auction`() {
        // GIVEN
        val auction = MongoAuction.random(id = null)

        // WHEN
        val savedAuction = auctionRepository.save(auction)

        // THEN
        assertEquals(auction.copy(id = savedAuction.id), savedAuction)
    }

    @Test
    fun `should find auction by id when auction with this id exists`() {
        // GIVEN
        val savedAuction = auctionRepository.save(MongoAuction.random(id = null))

        // WHEN
        val result = auctionRepository.findById(savedAuction.id.toString())

        // THEN
        assertEquals(savedAuction.id, result?.id)
    }

    @Test
    fun `should return null when there is no auction with this id`() {
        // WHEN
        val result = auctionRepository.findById(ObjectId().toString())

        // THEN
        assertNull(result, "Found auction must be null")
    }

    @Test
    fun `should return full auction with buyers and artwork when auction with this id exists`() {
        // GIVEN
        val savedArtist = userRepository.save(MongoUser.random(id = null))
        val savedArtwork = artworkRepository.save(MongoArtwork(artistId = savedArtist.id))
        val savedBuyer = userRepository.save(MongoUser.random(id = null, role = Role.BUYER))
        val buyers = listOf(
            MongoAuction.Bid.random(buyerId = savedBuyer.id!!.toHexString()),
        )
        val savedAuction = auctionRepository.save(MongoAuction(buyers = buyers, artworkId = savedArtwork.id))

        // WHEN
        val result = auctionRepository.findFullById(savedAuction.id.toString())

        // THEN
        assertEquals(savedArtwork.title, result?.artwork?.title)
        assertEquals(savedArtist.name, result?.artwork?.artist?.name)
        assertEquals(buyers.size, result?.buyers?.size)
        assertTrue(
            result?.buyers?.any { it.buyer?.name == savedBuyer.name } ?: false,
            "Buyer with name ${savedBuyer.name} must be found"
        )
    }

    @Test
    fun `should return null when auction with this id doesnt exists`() {
        // WHEN
        val result = artworkRepository.findFullById(ObjectId().toString())

        // THEN
        assertNull(result, "Found artwork must be null")
    }

    @Test
    fun `should return all auctions when they are exists`() {
        // GIVEN
        val auctions = listOf(MongoAuction.random(id = null), MongoAuction.random(id = null))
        auctions.forEach { auction -> auctionRepository.save(auction) }

        // WHEN
        val result = auctionRepository.findAll()

        // THEN
        assertTrue(result.size >= auctions.size, "Size of list must be at least ${result.size}")
        auctions.forEach { auction ->
            assertTrue(
                result.any { it.artworkId == auction.artworkId },
                "Auction with artworkId ${auction.artworkId} must be found"
            )
        }
    }

    @Test
    fun `should return all auctions with artwork and buyers when they are exists`() {
        // GIVEN
        val savedArtist = userRepository.save(MongoUser.random(id = null))
        val savedArtwork = artworkRepository.save(MongoArtwork(artistId = savedArtist.id))
        val savedBuyer = userRepository.save(MongoUser.random(id = null, role = Role.BUYER))
        val buyers = listOf(
            MongoAuction.Bid.random(buyerId = savedBuyer.id!!.toHexString()),
        )
        val auctions = listOf(MongoAuction(buyers = buyers, artworkId = savedArtwork.id))
        auctions.forEach { auction -> auctionRepository.save(auction) }

        // WHEN
        val result = auctionRepository.findFullAll()

        // THEN
        assertTrue(result.size >= auctions.size, "Size of list must be at least ${result.size}")
        assertTrue(
            result.any { it.buyers?.size == buyers.size && it.artwork?.artist?.name == savedArtist.name },
            "Auction with artwork artist ${savedArtist.name} and list of ${buyers.size} buyers not found"
        )
    }
}
