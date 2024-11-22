package ua.marchenko.artauction.artwork.controller.nats

import ua.marchenko.artauction.artwork.random
import ua.marchenko.artauction.artwork.toFullArtwork
import ua.marchenko.artauction.getRandomString
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
import ua.marchenko.core.artwork.enums.ArtworkStatus
import ua.marchenko.core.artwork.enums.ArtworkStyle
import ua.marchenko.internal.NatsSubject
import ua.marchenko.commonmodels.Error
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkByIdRequest
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkFullByIdRequest
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkFullByIdResponse
import ua.marchenko.artauction.user.random

class GetArtworkFullByIdNatsControllerTest : AbstractBaseIntegrationTest {

    @Autowired
    private lateinit var artworkRepository: ArtworkRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var natsPublisher: NatsMessagePublisher

    @Test
    fun `should return FindArtworkFullByIdResponse Success when artwork with this id exists`() {
        // GIVEN
        val savedArtist = userRepository.save(MongoUser.random(id = null)).block()
        val artwork = artworkRepository.save(
            MongoArtwork.random(
                id = null,
                artistId = savedArtist!!.id.toString(),
                style = ArtworkStyle.EXPRESSIONISM,
                status = ArtworkStatus.SOLD
            )
        ).block()!!.toFullArtwork(savedArtist)
        val request = FindArtworkByIdRequest.newBuilder().setId(artwork.id!!.toHexString()).build()
        val expectedResponse = FindArtworkFullByIdResponse.newBuilder().also { builder ->
            builder.successBuilder.setArtwork(artwork.toArtworkFullProto())
        }.build()

        // WHEN
        val result = natsPublisher.request(
            subject = NatsSubject.Artwork.FIND_BY_ID_FULL,
            payload = request,
            parser = FindArtworkFullByIdResponse.parser()
        )

        // THEN
        result.test()
            .expectNext(expectedResponse)
            .verifyComplete()
    }

    @Test
    fun `should return FindArtworkFullByIdResponse Failure when there is no artwork with this id`() {
        // GIVEN
        val id = getRandomString()
        val request = FindArtworkFullByIdRequest.newBuilder().setId(id).build()
        val expectedResponse = FindArtworkFullByIdResponse.newBuilder().also { builder ->
            builder.failureBuilder.setMessage(ERROR_MESSAGE_TEMPLATE.format(id))
            builder.failureBuilder.setNotFoundById(
                Error.getDefaultInstance()
            )
        }.build()

        // WHEN
        val result = natsPublisher.request(
            subject = NatsSubject.Artwork.FIND_BY_ID_FULL,
            payload = request,
            parser = FindArtworkFullByIdResponse.parser()
        )

        // THEN
        result.test()
            .expectNext(expectedResponse)
            .verifyComplete()
    }

    companion object {
        private const val ERROR_MESSAGE_TEMPLATE = "Artwork with ID %s not found"
    }
}
