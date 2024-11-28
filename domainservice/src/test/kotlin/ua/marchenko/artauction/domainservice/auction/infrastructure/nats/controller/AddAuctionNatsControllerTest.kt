package ua.marchenko.artauction.domainservice.auction.infrastructure.nats.controller

import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.kotlin.test.test
import systems.ajax.nats.publisher.api.NatsMessagePublisher
import ua.marchenko.artauction.domainservice.artwork.application.port.output.ArtworkRepositoryOutputPort
import ua.marchenko.artauction.domainservice.auction.infrastructure.AuctionProtoFixture
import ua.marchenko.artauction.domainservice.user.domain.User
import ua.marchenko.artauction.domainservice.artwork.domain.Artwork
import ua.marchenko.artauction.domainservice.artwork.domain.random
import ua.marchenko.artauction.domainservice.user.application.port.output.UserRepositoryOutputPort
import ua.marchenko.artauction.domainservice.utils.AbstractBaseIntegrationTest
import ua.marchenko.artauction.domainservice.user.domain.random
import ua.marchenko.internal.NatsSubject
import ua.marchenko.commonmodels.auction.Auction as AuctionProto
import ua.marchenko.internal.input.reqreply.auction.CreateAuctionResponse as CreateAuctionResponseProto

class AddAuctionNatsControllerTest : AbstractBaseIntegrationTest {

    @Autowired
    private lateinit var userRepository: UserRepositoryOutputPort

    @Autowired
    private lateinit var mongoArtworkRepository: ArtworkRepositoryOutputPort

    @Autowired
    private lateinit var natsPublisher: NatsMessagePublisher

    @Test
    fun `should save new auction and return AuctionResponse with data from CreateAuctionRequest`() {
        // GIVEN
        val savedArtist = userRepository.save(User.random(id = null)).block()
        val savedArtwork =
            mongoArtworkRepository.save(Artwork.random(artistId = savedArtist!!.id!!)).block()
        val request = AuctionProtoFixture.randomCreateAuctionRequestProto(artworkId = savedArtwork!!.id!!)
        val expectedResponse = CreateAuctionResponseProto.newBuilder().apply {
            successBuilder.setAuction(
                AuctionProto.newBuilder().apply {
                    artworkId = request.artworkId
                    startBid = request.startBid
                    startedAt = request.startedAt
                    finishedAt = request.finishedAt
                    addAllBuyers(emptyList())
                })
        }.build()

        // WHEN
        val result = natsPublisher.request(
            subject = NatsSubject.Auction.CREATE,
            payload = request,
            parser = CreateAuctionResponseProto.parser()
        )

        // THEN
        result.test()
            .assertNext {
                assertEquals(
                    expectedResponse.success.auction,
                    it.toBuilder().successBuilder.auctionBuilder.clearId().build()
                )
            }.verifyComplete()
    }
}
