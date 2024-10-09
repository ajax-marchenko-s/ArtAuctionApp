package ua.marchenko.artauction.auction.repository

import auction.random
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.test.Test
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.beans.factory.annotation.Autowired
import reactor.kotlin.test.test
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
        savedAuction.test()
            .assertNext { auctionFromMono -> assertEquals(auction.copy(id = auctionFromMono.id), auctionFromMono) }
            .verifyComplete()
    }

    @Test
    fun `should find auction by id when auction with this id exists`() {
        // GIVEN
        val savedAuction = auctionRepository.save(
            MongoAuction.random(
                id = null,
                startedAt = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS),
                finishedAt = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MILLIS)
            )
        ).block()

        // WHEN
        val result = auctionRepository.findById(savedAuction?.id.toString())

        // THEN
        result.test()
            .expectNext(savedAuction)
            .verifyComplete()
    }

    @Test
    fun `should return null when there is no auction with this id`() {
        // WHEN
        val result = auctionRepository.findById(ObjectId().toString())

        // THEN
        result.test()
            .verifyComplete()
    }

    @Test
    fun `should return full auction with buyers and artwork when auction with this id exists`() {
        // GIVEN
        val savedArtist = userRepository.save(MongoUser.random(id = null)).block()
        val savedArtwork = artworkRepository.save(MongoArtwork(artistId = savedArtist?.id)).block()
        val savedBuyer = userRepository.save(MongoUser.random(id = null, role = Role.BUYER)).block()

        val buyers = listOf(
            MongoAuction.Bid.random(buyerId = savedBuyer?.id!!.toHexString()),
        )
        val savedAuction = auctionRepository.save(MongoAuction(buyers = buyers, artworkId = savedArtwork?.id)).block()

        // WHEN
        val result = auctionRepository.findFullById(savedAuction?.id.toString())

        // THEN
        result.test()
            .assertNext { auction ->
                assertEquals(savedArtwork?.title, auction?.artwork?.title)
                assertEquals(savedArtist?.name, auction?.artwork?.artist?.name)
                assertEquals(buyers.size, auction?.buyers?.size)
                assertTrue(
                    auction?.buyers?.any { it.buyer?.name == savedBuyer.name } ?: false,
                    "Buyer with name ${savedBuyer.name} must be found"
                )
            }
            .verifyComplete()
    }

    @Test
    fun `should return null when auction with this id doesnt exists`() {
        // WHEN
        val result = artworkRepository.findFullById(ObjectId().toString())

        // THEN
        result.test()
            .verifyComplete()
    }

    @Test
    fun `should return all auctions when they are exists`() {
        // GIVEN
        val auctions = listOf(
            auctionRepository.save(
                MongoAuction.random(
                    id = null, startedAt = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS),
                    finishedAt = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MILLIS)
                )
            ).block(),
            auctionRepository.save(
                MongoAuction.random(
                    id = null, startedAt = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS),
                    finishedAt = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MILLIS)
                )
            ).block()
        )

        // WHEN
        val result = auctionRepository.findAll().collectList()

        // THEN
        result.test()
            .expectNextMatches { it.containsAll(auctions) }
            .`as`("Auction with id ${auctions[0]?.id} and ${auctions[1]?.id} must be found")
            .verifyComplete()
    }

    @Test
    fun `should return all auctions with artwork and buyers when they are exists`() {
        // GIVEN
        val savedArtist = userRepository.save(MongoUser.random(id = null)).block()
        val savedArtwork = artworkRepository.save(MongoArtwork(artistId = savedArtist?.id)).block()
        val savedBuyer = userRepository.save(MongoUser.random(id = null, role = Role.BUYER)).block()
        val buyers = listOf(
            MongoAuction.Bid.random(buyerId = savedBuyer?.id!!.toHexString()),
        )
        val auctions =
            listOf(auctionRepository.save(MongoAuction(buyers = buyers, artworkId = savedArtwork?.id)).block())

        // WHEN
        val result = auctionRepository.findFullAll().collectList()

        // THEN
        result.test()
            .expectNextMatches { foundAuctions ->
                foundAuctions.any {
                    it.id == auctions[0]?.id &&
                            it.buyers?.size == buyers.size &&
                            it.artwork?.artist?.name == savedArtist?.name
                }
            }
            .`as`("Auction with id ${auctions[0]?.id} must be found")
            .verifyComplete()
    }
}

