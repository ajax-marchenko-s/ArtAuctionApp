package ua.marchenko.artauction.auction.controller.nats

import artwork.random
import java.time.Clock
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
import ua.marchenko.artauction.common.mongodb.id.toObjectId
import ua.marchenko.core.artwork.enums.ArtworkStyle
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
        val artworks = listOf(
            artworkRepository.save(MongoArtwork.random(id = null, style = ArtworkStyle.CUBISM)).block()!!
                .toArtworkProto(),
            artworkRepository.save(MongoArtwork.random(id = null, style = ArtworkStyle.SURREALISM)).block()!!
                .toArtworkProto(),
        )
        val auctions = listOf(
            auctionRepository.save(MongoAuction(artworkId = artworks[0].id.toObjectId())).block()!!
                .toAuctionProto(clock),
            auctionRepository.save(MongoAuction(artworkId = artworks[1].id.toObjectId())).block()!!
                .toAuctionProto(clock),
        )

        val request = FindAllAuctionsRequestProto.newBuilder().setPage(0).setLimit(100).build()

        // WHEN
        val result = doRequest(
            subject = NatsSubject.AuctionNatsSubject.FIND_ALL,
            request = request,
            parser = FindAllAuctionsResponseProto.parser()
        )

        // THEN
        val foundAuctions = result.success.auctionsList
        assertTrue(foundAuctions.containsAll(auctions), "Artworks $artworks not found in returned list")
    }
}
