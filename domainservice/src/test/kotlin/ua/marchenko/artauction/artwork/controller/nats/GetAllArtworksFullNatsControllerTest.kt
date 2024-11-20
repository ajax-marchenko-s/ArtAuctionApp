package ua.marchenko.artauction.artwork.controller.nats

import ua.marchenko.artauction.artwork.random
import ua.marchenko.artauction.artwork.toFullArtwork
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.kotlin.test.test
import systems.ajax.nats.publisher.api.NatsMessagePublisher
import ua.marchenko.artauction.artwork.mapper.toArtworkFullProto
import ua.marchenko.artauction.artwork.model.MongoArtwork
import ua.marchenko.artauction.artwork.repository.ArtworkRepository
import ua.marchenko.artauction.common.AbstractBaseIntegrationTest
import ua.marchenko.artauction.user.model.MongoUser
import ua.marchenko.artauction.user.repository.UserRepository
import ua.marchenko.core.artwork.enums.ArtworkStyle
import ua.marchenko.internal.NatsSubject
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksFullRequest
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksFullResponse
import ua.marchenko.artauction.user.random

class GetAllArtworksFullNatsControllerTest : AbstractBaseIntegrationTest {

    @Autowired
    private lateinit var artworkRepository: ArtworkRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var natsPublisher: NatsMessagePublisher

    @Test
    fun `should return all full artworks when they are exists`() {
        // GIVEN
        val savedArtist = userRepository.save(MongoUser.random(id = null)).block()
        val artworks = listOf(
            artworkRepository.save(
                MongoArtwork.random(
                    artistId = savedArtist!!.id.toString(),
                    style = ArtworkStyle.REALISM
                )
            ).block()!!
                .toFullArtwork(savedArtist).toArtworkFullProto(),
            artworkRepository.save(
                MongoArtwork.random(
                    artistId = savedArtist.id.toString(),
                    style = ArtworkStyle.IMPRESSIONISM
                )
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
