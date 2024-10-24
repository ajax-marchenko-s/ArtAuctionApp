package ua.marchenko.artauction.auction.repository

import artwork.toFullArtwork
import auction.random
import auction.toFullAuction
import auction.toFullBid
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
import ua.marchenko.artauction.user.model.MongoUser
import ua.marchenko.artauction.user.repository.UserRepository
import ua.marchenko.core.user.enums.Role
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
        val result = auctionRepository.findById(savedAuction!!.id.toString())

        // THEN
        result.test()
            .expectNext(savedAuction)
            .verifyComplete()
    }

    @Test
    fun `should return empty when there is no auction with this id`() {
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
        val savedArtworkFull = artworkRepository.save(MongoArtwork(artistId = savedArtist!!.id))
            .block()!!.toFullArtwork(savedArtist)
        val savedBuyer = userRepository.save(MongoUser.random(id = null, role = Role.BUYER)).block()
        val buyers = listOf(MongoAuction.Bid.random(buyerId = savedBuyer!!.id!!.toHexString()))

        val auction = auctionRepository.save(
            MongoAuction(
                id = null,
                buyers = buyers,
                artworkId = savedArtworkFull.id,
                startedAt = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS),
                finishedAt = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MILLIS)
            )
        ).block()!!.toFullAuction(savedArtworkFull, buyers.map { it.toFullBid(savedBuyer) })

        // WHEN
        val result = auctionRepository.findFullById(auction.id.toString())

        // THEN
        result.test()
            .expectNext(auction)
            .verifyComplete()
    }

    @Test
    fun `should return empty when auction with this id doesnt exists`() {
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
        val result = auctionRepository.findAll(page = 0, limit = 100).collectList()

        // THEN
        result.test()
            .assertNext { assertTrue(it.containsAll(auctions), "Auctions $auctions must be found") }
            .verifyComplete()
    }

    @Test
    fun `should return all auctions with artwork and buyers when they are exists`() {
        // GIVEN
        val savedArtist = userRepository.save(MongoUser.random(id = null)).block()
        val savedArtworkFull = artworkRepository.save(MongoArtwork(artistId = savedArtist!!.id))
            .block()!!.toFullArtwork(savedArtist)
        val savedBuyer = userRepository.save(MongoUser.random(id = null, role = Role.BUYER)).block()
        val buyers = listOf(MongoAuction.Bid.random(buyerId = savedBuyer!!.id!!.toHexString()))

        val auctions = listOf(
            auctionRepository.save(
                MongoAuction(
                    id = null,
                    buyers = buyers,
                    artworkId = savedArtworkFull.id,
                    startedAt = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS),
                    finishedAt = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MILLIS)
                )
            ).block()!!.toFullAuction(savedArtworkFull, buyers.map { it.toFullBid(savedBuyer) })
        )

        // WHEN
        val result = auctionRepository.findFullAll(page = 0, limit = 100).collectList()

        // THEN
        result.test()
            .assertNext { assertTrue(it.containsAll(auctions), "Auctions $auctions must be found") }
            .verifyComplete()
    }
}

