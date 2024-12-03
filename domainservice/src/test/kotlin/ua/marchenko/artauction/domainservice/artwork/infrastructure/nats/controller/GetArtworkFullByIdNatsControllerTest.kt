package ua.marchenko.artauction.domainservice.artwork.infrastructure.nats.controller

import ua.marchenko.artauction.domainservice.artwork.getRandomString
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import reactor.kotlin.test.test
import systems.ajax.nats.publisher.api.NatsMessagePublisher
import ua.marchenko.artauction.domainservice.artwork.application.port.output.ArtworkRepositoryOutputPort
import ua.marchenko.artauction.domainservice.artwork.domain.random
import ua.marchenko.artauction.domainservice.artwork.domain.toFullArtwork
import ua.marchenko.internal.NatsSubject
import ua.marchenko.commonmodels.Error
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkByIdRequest
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkFullByIdRequest
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkFullByIdResponse
import ua.marchenko.artauction.domainservice.artwork.domain.CreateArtwork
import ua.marchenko.artauction.domainservice.artwork.infrastructure.nats.mapper.toArtworkFullProto
import ua.marchenko.artauction.domainservice.user.application.port.output.UserRepositoryOutputPort
import ua.marchenko.artauction.domainservice.user.domain.CreateUser
import ua.marchenko.artauction.domainservice.utils.AbstractBaseIntegrationTest
import ua.marchenko.artauction.domainservice.user.domain.random

class GetArtworkFullByIdNatsControllerTest : AbstractBaseIntegrationTest {

    @Autowired
    @Qualifier("mongoArtworkRepository")
    private lateinit var artworkRepository: ArtworkRepositoryOutputPort

    @Autowired
    private lateinit var userRepository: UserRepositoryOutputPort

    @Autowired
    private lateinit var natsPublisher: NatsMessagePublisher

    @Test
    fun `should return FindArtworkFullByIdResponse Success when artwork with this id exists`() {
        // GIVEN
        val savedArtist = userRepository.save(CreateUser.random()).block()
        val artwork = artworkRepository.save(CreateArtwork.random(artistId = savedArtist!!.id)).block()!!
            .toFullArtwork(savedArtist)
        val request = FindArtworkByIdRequest.newBuilder().setId(artwork.id).build()
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
