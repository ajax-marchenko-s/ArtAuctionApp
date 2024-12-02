package ua.marchenko.artauction.domainservice.auction.infrastructure.mongo.repository

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.test.Test
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.beans.factory.annotation.Autowired
import reactor.kotlin.test.test
import ua.marchenko.artauction.domainservice.auction.common.toFullAuction
import ua.marchenko.artauction.domainservice.auction.common.toFullBid
import ua.marchenko.artauction.domainservice.auction.domain.Auction
import ua.marchenko.artauction.domainservice.auction.domain.random
import ua.marchenko.artauction.domainservice.user.infrastructure.mongo.repository.MongoUserRepository
import ua.marchenko.artauction.domainservice.artwork.domain.CreateArtwork
import ua.marchenko.artauction.domainservice.artwork.domain.random
import ua.marchenko.artauction.domainservice.artwork.domain.toFullArtwork
import ua.marchenko.artauction.domainservice.artwork.infrastructure.mongo.repository.MongoArtworkRepository
import ua.marchenko.artauction.domainservice.auction.domain.CreateAuction
import ua.marchenko.artauction.domainservice.user.domain.CreateUser
import ua.marchenko.artauction.domainservice.utils.AbstractBaseIntegrationTest
import ua.marchenko.artauction.domainservice.user.domain.random

class MongoAuctionRepositoryTest : AbstractBaseIntegrationTest {

    @Autowired
    private lateinit var auctionRepository: MongoAuctionRepository

    @Autowired
    private lateinit var artworkRepository: MongoArtworkRepository

    @Autowired
    private lateinit var userRepository: MongoUserRepository

    @Test
    fun `should save auction`() {
        // GIVEN
        val createdAuction = CreateAuction.random()
        val expectedAuction = Auction(
            id = EMPTY_STRING,
            artworkId = createdAuction.artworkId,
            startBid = createdAuction.startBid,
            buyers = createdAuction.buyers,
            startedAt = createdAuction.startedAt,
            finishedAt = createdAuction.finishedAt,
        )

        // WHEN
        val savedAuction = auctionRepository.save(createdAuction)

        // THEN
        savedAuction.test()
            .assertNext { auctionFromMono ->
                assertEquals(
                    expectedAuction.copy(id = auctionFromMono.id),
                    auctionFromMono
                )
            }
            .verifyComplete()
    }

    @Test
    fun `should find auction by id when auction with this id exists`() {
        // GIVEN
        val savedAuction = auctionRepository.save(
            CreateAuction.random(
                startedAt = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS),
                finishedAt = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MILLIS)
            )
        ).block()

        // WHEN
        val result = auctionRepository.findById(savedAuction!!.id.)

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
        val savedArtist = userRepository.save(CreateUser.random()).block()
        val savedArtworkFull = artworkRepository.save(CreateArtwork.random(artistId = savedArtist!!.id))
            .block()!!.toFullArtwork(savedArtist)
        val savedBuyer = userRepository.save(CreateUser.random()).block()
        val buyers = listOf(Auction.Bid.random(buyerId = savedBuyer!!.id))

        val auction = auctionRepository.save(
            CreateAuction.random(
                buyers = buyers,
                artworkId = savedArtworkFull.id,
                startedAt = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS),
                finishedAt = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MILLIS),
            )
        ).block()!!.toFullAuction(savedArtworkFull, buyers.map { it.toFullBid(savedBuyer) })

        // WHEN
        val result = auctionRepository.findFullById(auction.id)

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
        val auctions = List(2) {
            auctionRepository.save(
                CreateAuction.random(
                    startedAt = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS),
                    finishedAt = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MILLIS)
                )
            ).block()
        }

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
        val savedArtist = userRepository.save(CreateUser.random()).block()
        val savedArtworkFull = artworkRepository.save(CreateArtwork.random(artistId = savedArtist!!.id))
            .block()!!.toFullArtwork(savedArtist)
        val savedBuyer = userRepository.save(CreateUser.random()).block()
        val buyers = listOf(Auction.Bid.random(buyerId = savedBuyer!!.id))

        val auctions = listOf(
            auctionRepository.save(
                CreateAuction.random(
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

    companion object {
        private const val EMPTY_STRING = ""
    }
}
