package ua.marchenko.artauction.domainservice.auction.infrastructure.nats.controller

import java.time.Clock
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import reactor.kotlin.test.test
import systems.ajax.nats.publisher.api.NatsMessagePublisher
import ua.marchenko.artauction.domainservice.artwork.application.port.output.ArtworkRepositoryOutputPort
import ua.marchenko.artauction.domainservice.auction.domain.random
import ua.marchenko.artauction.domainservice.artwork.domain.CreateArtwork
import ua.marchenko.artauction.domainservice.artwork.domain.random
import ua.marchenko.artauction.domainservice.auction.application.port.output.AuctionRepositoryOutputPort
import ua.marchenko.artauction.domainservice.auction.domain.CreateAuction
import ua.marchenko.artauction.domainservice.auction.infrastructure.common.mapper.CommonMapper.toAuctionProto
import ua.marchenko.artauction.domainservice.utils.AbstractBaseIntegrationTest
import ua.marchenko.internal.NatsSubject
import ua.marchenko.internal.input.reqreply.auction.FindAuctionByIdRequest as FindAuctionByIdRequestProto
import ua.marchenko.internal.input.reqreply.auction.FindAuctionByIdResponse as FindAuctionByIdResponseProto

class GetAuctionByIdNatsControllerTest : AbstractBaseIntegrationTest {

    @Autowired
    @Qualifier("mongoArtworkRepository")
    private lateinit var artworkRepository: ArtworkRepositoryOutputPort

    @Autowired
    private lateinit var auctionRepository: AuctionRepositoryOutputPort

    @Autowired
    private lateinit var natsPublisher: NatsMessagePublisher

    @Autowired
    private lateinit var clock: Clock

    @Test
    fun `should return FindAuctionByIdResponse Success when auction with this id exists`() {
        // GIVEN
        val artwork = artworkRepository.save(CreateArtwork.random()).block()!!
        val auction = auctionRepository.save(
            CreateAuction.random(
                artworkId = artwork.id,
                startedAt = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS),
                finishedAt = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MILLIS)
            )
        ).block()!!
        val request = FindAuctionByIdRequestProto.newBuilder().setId(auction.id).build()
        val expectedResponse = FindAuctionByIdResponseProto.newBuilder().apply {
            successBuilder.setAuction(auction.toAuctionProto(clock))
        }.build()

        // WHEN
        val result = natsPublisher.request(
            subject = NatsSubject.Auction.FIND_BY_ID,
            payload = request,
            parser = FindAuctionByIdResponseProto.parser()
        )

        // THEN
        result.test()
            .expectNext(expectedResponse)
            .verifyComplete()
    }
}
