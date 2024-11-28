package ua.marchenko.artauction.domainservice.artwork.infrastructure.nats.controller

import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import reactor.kotlin.test.test
import systems.ajax.nats.publisher.api.NatsMessagePublisher
import ua.marchenko.artauction.domainservice.artwork.application.port.output.ArtworkRepositoryOutputPort
import ua.marchenko.artauction.domainservice.artwork.domain.random
import ua.marchenko.artauction.domainservice.artwork.domain.toFullArtwork
import ua.marchenko.artauction.domainservice.user.domain.User
import ua.marchenko.internal.NatsSubject
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksFullRequest
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksFullResponse
import ua.marchenko.artauction.domainservice.artwork.domain.Artwork
import ua.marchenko.artauction.domainservice.artwork.infrastructure.nats.mapper.toArtworkFullProto
import ua.marchenko.artauction.domainservice.user.application.port.output.UserRepositoryOutputPort
import ua.marchenko.artauction.domainservice.utils.AbstractBaseIntegrationTest
import ua.marchenko.artauction.domainservice.user.domain.random

class GetAllArtworksFullNatsControllerTest : AbstractBaseIntegrationTest {

    @Autowired
    @Qualifier("mongoArtworkRepository")
    private lateinit var artworkRepository: ArtworkRepositoryOutputPort


    @Autowired
    private lateinit var userRepository: UserRepositoryOutputPort

    @Autowired
    private lateinit var natsPublisher: NatsMessagePublisher

    @Test
    fun `should return all full artworks when they are exists`() {
        // GIVEN
        val savedArtist = userRepository.save(User.random(id = null)).block()
        val artworks = listOf(
            artworkRepository.save(
                Artwork.random(artistId = savedArtist!!.id.toString())
            ).block()!!
                .toFullArtwork(savedArtist).toArtworkFullProto(),
            artworkRepository.save(
                Artwork.random(artistId = savedArtist.id.toString())
            ).block()!!
                .toFullArtwork(savedArtist).toArtworkFullProto(),
        )
        val request = FindAllArtworksFullRequest.newBuilder().setPage(0).setLimit(100).build()

        // WHEN
        val result = natsPublisher.request(
            subject = NatsSubject.Artwork.FIND_ALL_FULL,
            payload = request,
            parser = FindAllArtworksFullResponse.parser()
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
