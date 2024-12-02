package ua.marchenko.artauction.domainservice.artwork.infrastructure.nats.controller

import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import reactor.kotlin.test.test
import systems.ajax.nats.publisher.api.NatsMessagePublisher
import ua.marchenko.artauction.domainservice.artwork.application.port.output.ArtworkRepositoryOutputPort
import ua.marchenko.artauction.domainservice.artwork.domain.random
import ua.marchenko.artauction.domainservice.artwork.domain.CreateArtwork
import ua.marchenko.artauction.domainservice.artwork.infrastructure.nats.mapper.toArtworkProto
import ua.marchenko.artauction.domainservice.utils.AbstractBaseIntegrationTest
import ua.marchenko.internal.NatsSubject
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksRequest
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksResponse

class GetAllArtworksNatsControllerTest : AbstractBaseIntegrationTest {

    @Autowired
    @Qualifier("mongoArtworkRepository")
    private lateinit var artworkRepository: ArtworkRepositoryOutputPort

    @Autowired
    private lateinit var natsPublisher: NatsMessagePublisher

    @Test
    fun `should return all artworks when they are exists`() {
        // GIVEN
        val artworks = List (2) { artworkRepository.save(CreateArtwork.random()).block()!!.toArtworkProto() }
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
