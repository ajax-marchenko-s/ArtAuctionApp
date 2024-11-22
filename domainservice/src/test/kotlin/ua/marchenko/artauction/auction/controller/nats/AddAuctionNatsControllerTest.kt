package ua.marchenko.artauction.auction.controller.nats

import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.kotlin.test.test
import systems.ajax.nats.publisher.api.NatsMessagePublisher
import ua.marchenko.artauction.artwork.model.MongoArtwork
import ua.marchenko.artauction.artwork.random
import ua.marchenko.artauction.artwork.repository.ArtworkRepository
import ua.marchenko.artauction.auction.AuctionProtoFixture
import ua.marchenko.artauction.common.AbstractBaseIntegrationTest
import ua.marchenko.artauction.user.model.MongoUser
import ua.marchenko.artauction.user.random
import ua.marchenko.artauction.user.repository.UserRepository
import ua.marchenko.internal.NatsSubject
import ua.marchenko.commonmodels.auction.Auction as AuctionProto
import ua.marchenko.internal.input.reqreply.auction.CreateAuctionResponse as CreateAuctionResponseProto

class AddAuctionNatsControllerTest : AbstractBaseIntegrationTest {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var artworkRepository: ArtworkRepository

    @Autowired
    private lateinit var natsPublisher: NatsMessagePublisher

    @Test
    fun `should save new auction and return AuctionResponse with data from CreateAuctionRequest`() {
        // GIVEN
        val savedArtist = userRepository.save(MongoUser.random(id = null)).block()
        val savedArtwork =
            artworkRepository.save(MongoArtwork.random(artistId = savedArtist!!.id!!.toHexString())).block()
        val request = AuctionProtoFixture.randomCreateAuctionRequestProto(artworkId = savedArtwork!!.id!!.toHexString())
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
