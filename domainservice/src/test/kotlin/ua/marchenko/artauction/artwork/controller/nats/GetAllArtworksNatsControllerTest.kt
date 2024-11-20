package ua.marchenko.artauction.artwork.controller.nats

import ua.marchenko.artauction.artwork.random
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.kotlin.test.test
import systems.ajax.nats.publisher.api.NatsMessagePublisher
import ua.marchenko.artauction.artwork.mapper.toArtworkProto
import ua.marchenko.artauction.artwork.model.MongoArtwork
import ua.marchenko.artauction.artwork.repository.ArtworkRepository
import ua.marchenko.artauction.common.AbstractBaseIntegrationTest
import ua.marchenko.core.artwork.enums.ArtworkStyle
import ua.marchenko.internal.NatsSubject
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksRequest
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksResponse

class GetAllArtworksNatsControllerTest : AbstractBaseIntegrationTest {

    @Autowired
    private lateinit var artworkRepository: ArtworkRepository

    @Autowired
    private lateinit var natsPublisher: NatsMessagePublisher

    @Test
    fun `should return all artworks when they are exists`() {
        // GIVEN
        val artworks = listOf(
            artworkRepository.save(MongoArtwork.random(id = null, style = ArtworkStyle.CUBISM)).block()!!
                .toArtworkProto(),
            artworkRepository.save(MongoArtwork.random(id = null, style = ArtworkStyle.SURREALISM)).block()!!
                .toArtworkProto(),
        )
        val request = FindAllArtworksRequest.newBuilder().setPage(0).setLimit(100).build()

        // WHEN
        val result = natsPublisher.request(
            subject = NatsSubject.Artwork.FIND_ALL,
            payload = request,
            parser = FindAllArtworksResponse.parser()
        )

        // THEN
        result.test()
            .assertNext {
                assertTrue(
                    it.success.artworksList.containsAll(artworks),
                    "Artworks $artworks not found in returned list"
                )
            }
            .verifyComplete()
    }
}
