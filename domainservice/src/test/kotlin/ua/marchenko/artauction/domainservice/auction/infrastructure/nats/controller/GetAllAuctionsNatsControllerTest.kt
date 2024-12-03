package ua.marchenko.artauction.domainservice.auction.infrastructure.nats.controller

import java.time.Clock
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import reactor.kotlin.test.test
import systems.ajax.nats.publisher.api.NatsMessagePublisher
import ua.marchenko.artauction.domainservice.artwork.application.port.output.ArtworkRepositoryOutputPort
import ua.marchenko.artauction.domainservice.auction.domain.random
import ua.marchenko.artauction.domainservice.artwork.domain.CreateArtwork
import ua.marchenko.artauction.domainservice.artwork.domain.random
import ua.marchenko.artauction.domainservice.artwork.infrastructure.nats.mapper.toArtworkProto
import ua.marchenko.artauction.domainservice.auction.application.port.output.AuctionRepositoryOutputPort
import ua.marchenko.artauction.domainservice.auction.domain.CreateAuction
import ua.marchenko.artauction.domainservice.auction.infrastructure.common.mapper.CommonMapper.toAuctionProto
import ua.marchenko.artauction.domainservice.utils.AbstractBaseIntegrationTest
import ua.marchenko.internal.NatsSubject
import ua.marchenko.internal.input.reqreply.auction.FindAllAuctionsRequest as FindAllAuctionsRequestProto
import ua.marchenko.internal.input.reqreply.auction.FindAllAuctionsResponse as FindAllAuctionsResponseProto

class GetAllAuctionsNatsControllerTest : AbstractBaseIntegrationTest {

    @Autowired
    @Qualifier("redisArtworkRepository")
    private lateinit var artworkRepository: ArtworkRepositoryOutputPort

    @Autowired
    private lateinit var auctionRepository: AuctionRepositoryOutputPort

    @Autowired
    private lateinit var natsPublisher: NatsMessagePublisher

    @Autowired
    private lateinit var clock: Clock

    @Test
    fun `should return all auctions when they are exists`() {
        // GIVEN
        val artworks = List(2) { artworkRepository.save(CreateArtwork.random()).block()!!.toArtworkProto() }
        val auctions = artworks.map { artwork ->
            auctionRepository.save(
                CreateAuction.random(
                    artworkId = artwork.id!!,
                    startedAt = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS),
                    finishedAt = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MILLIS)
                )
            ).block()!!.toAuctionProto(clock)
        }
        val request = FindAllAuctionsRequestProto.newBuilder().apply {
            page = START_PAGE
            limit = Int.MAX_VALUE
        }.build()

        // WHEN
        val result = natsPublisher.request(
            subject = NatsSubject.Auction.FIND_ALL,
            payload = request,
            parser = FindAllAuctionsResponseProto.parser()
        )

        // THEN
        result.test()
            .assertNext {
                assertTrue(
                    it.success.auctionsList.containsAll(auctions),
                    "Auctions $auctions not found in returned list"
                )
            }
            .verifyComplete()
    }

    companion object {
        private const val START_PAGE = 0
    }
}
