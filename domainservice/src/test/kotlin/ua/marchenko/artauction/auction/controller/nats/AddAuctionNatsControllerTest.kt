package ua.marchenko.artauction.auction.controller.nats

import artwork.random
import auction.AuctionProtoFixture
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import ua.marchenko.artauction.artwork.model.MongoArtwork
import ua.marchenko.artauction.artwork.repository.ArtworkRepository
import ua.marchenko.artauction.common.AbstractBaseNatsControllerTest
import ua.marchenko.artauction.user.model.MongoUser
import ua.marchenko.artauction.user.repository.UserRepository
import ua.marchenko.internal.NatsSubject
import ua.marchenko.commonmodels.auction.Auction as AuctionProto
import ua.marchenko.internal.input.reqreply.auction.CreateAuctionResponse as CreateAuctionResponseProto
import user.random

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
        val expectedResponse = CreateAuctionResponseProto.newBuilder().also { builder ->
            builder.successBuilder.setAuction(
                AuctionProto.newBuilder().also {
                    it.artworkId = request.artworkId
                    it.startBid = request.startBid
                    it.startedAt = request.startedAt
                    it.finishedAt = request.finishedAt
                    it.addAllBuyers(emptyList())
                })
        }.build()

        // WHEN
        val result = doRequest(
            subject = NatsSubject.AuctionNatsSubject.CREATE,
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
