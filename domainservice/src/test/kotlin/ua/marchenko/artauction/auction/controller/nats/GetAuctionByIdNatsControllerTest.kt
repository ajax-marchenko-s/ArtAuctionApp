package ua.marchenko.artauction.auction.controller.nats

import ua.marchenko.artauction.artwork.random
import ua.marchenko.artauction.auction.random
import java.time.Clock
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import ua.marchenko.artauction.artwork.model.MongoArtwork
import ua.marchenko.artauction.artwork.repository.ArtworkRepository
import ua.marchenko.artauction.auction.mapper.toAuctionProto
import ua.marchenko.artauction.auction.model.MongoAuction
import ua.marchenko.artauction.auction.repository.AuctionRepository
import ua.marchenko.artauction.common.AbstractBaseNatsControllerTest
import ua.marchenko.internal.NatsSubject
import ua.marchenko.internal.input.reqreply.auction.FindAuctionByIdRequest as FindAuctionByIdRequestProto
import ua.marchenko.internal.input.reqreply.auction.FindAuctionByIdResponse as FindAuctionByIdResponseProto

class GetAuctionByIdNatsControllerTest : AbstractBaseNatsControllerTest() {

    @Autowired
    @Qualifier("mongoArtworkRepository")
    private lateinit var artworkRepository: ArtworkRepository

    @Autowired
    private lateinit var auctionRepository: AuctionRepository

    @Autowired
    private lateinit var clock: Clock

    @Test
    fun `should return FindAuctionByIdResponse Success when auction with this id exists`() {
        // GIVEN
        val artwork = artworkRepository.save(MongoArtwork.random(id = null)).block()!!
        val auction = auctionRepository.save(
            MongoAuction.random(
                id = null,
                artworkId = artwork.id!!.toHexString(),
                startedAt = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS),
                finishedAt = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MILLIS)
            )
        ).block()!!
        val request = FindAuctionByIdRequestProto.newBuilder().setId(auction.id!!.toHexString()).build()
        val expectedResponse = FindAuctionByIdResponseProto.newBuilder().apply {
            successBuilder.setAuction(auction.toAuctionProto(clock))
        }.build()

        // WHEN
        val result = doRequest(
            subject = NatsSubject.Auction.FIND_BY_ID,
            request = request,
            parser = FindAuctionByIdResponseProto.parser()
        )

        // THEN
        assertEquals(expectedResponse, result)
    }
}
