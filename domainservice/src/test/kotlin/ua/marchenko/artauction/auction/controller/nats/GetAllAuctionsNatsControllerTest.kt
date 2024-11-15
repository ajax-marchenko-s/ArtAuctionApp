package ua.marchenko.artauction.auction.controller.nats

import artwork.random
import auction.random
import java.time.Clock
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import ua.marchenko.artauction.artwork.mapper.toArtworkProto
import ua.marchenko.artauction.artwork.model.MongoArtwork
import ua.marchenko.artauction.artwork.repository.ArtworkRepository
import ua.marchenko.artauction.auction.mapper.toAuctionProto
import ua.marchenko.artauction.auction.model.MongoAuction
import ua.marchenko.artauction.auction.repository.AuctionRepository
import ua.marchenko.artauction.common.AbstractBaseNatsControllerTest
import ua.marchenko.internal.NatsSubject
import ua.marchenko.internal.input.reqreply.auction.FindAllAuctionsRequest as FindAllAuctionsRequestProto
import ua.marchenko.internal.input.reqreply.auction.FindAllAuctionsResponse as FindAllAuctionsResponseProto

class GetAllAuctionsNatsControllerTest : AbstractBaseNatsControllerTest() {

    @Autowired
    private lateinit var artworkRepository: ArtworkRepository

    @Autowired
    private lateinit var auctionRepository: AuctionRepository

    @Autowired
    private lateinit var clock: Clock

    @Test
    fun `should return all auctions when they are exists`() {
        // GIVEN
        val artworks = List(2) { artworkRepository.save(MongoArtwork.random(id = null)).block()!!.toArtworkProto() }
        val auctions = artworks.map { artwork ->
            auctionRepository.save(
                MongoAuction.random(
                    id = null,
                    artworkId = artwork.id!!,
                    startedAt = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS),
                    finishedAt = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MILLIS)
                )
            ).block()!!.toAuctionProto(clock)
        }
        val request = FindAllAuctionsRequestProto.newBuilder().also {
            it.page = START_PAGE
            it.limit = Int.MAX_VALUE
        }.build()

        // WHEN
        val result = doRequest(
            subject = NatsSubject.Auction.FIND_ALL,
            request = request,
            parser = FindAllAuctionsResponseProto.parser()
        )

        // THEN
        val foundAuctions = result.success.auctionsList
        assertTrue(foundAuctions.containsAll(auctions), "Auctions $auctions not found in returned list")
    }

    companion object {
        private const val START_PAGE = 0
    }
}
