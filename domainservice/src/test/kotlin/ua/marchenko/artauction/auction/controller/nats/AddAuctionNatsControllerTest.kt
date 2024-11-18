package ua.marchenko.artauction.auction.controller.nats

import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import ua.marchenko.artauction.artwork.model.MongoArtwork
import ua.marchenko.artauction.artwork.random
import ua.marchenko.artauction.artwork.repository.ArtworkRepository
import ua.marchenko.artauction.auction.AuctionProtoFixture
import ua.marchenko.artauction.common.AbstractBaseNatsControllerTest
import ua.marchenko.artauction.user.model.MongoUser
import ua.marchenko.artauction.user.random
import ua.marchenko.artauction.user.repository.UserRepository
import ua.marchenko.internal.NatsSubject
import ua.marchenko.commonmodels.auction.Auction as AuctionProto
import ua.marchenko.internal.input.reqreply.auction.CreateAuctionResponse as CreateAuctionResponseProto

class AddAuctionNatsControllerTest : AbstractBaseNatsControllerTest() {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var artworkRepository: ArtworkRepository

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
        val result = doRequest(
            subject = NatsSubject.Auction.CREATE,
            request = request,
            parser = CreateAuctionResponseProto.parser()
        )

        // THEN
        assertEquals(
            expectedResponse.success.auction,
            result.toBuilder().successBuilder.auctionBuilder.clearId().build()
        )
    }
}
