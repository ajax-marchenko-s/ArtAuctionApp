package ua.marchenko.artauction.domainservice.artwork.infrastructure.nats.controller

import ua.marchenko.artauction.domainservice.artwork.getRandomString
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import reactor.kotlin.test.test
import systems.ajax.nats.publisher.api.NatsMessagePublisher
import ua.marchenko.artauction.domainservice.artwork.application.port.output.ArtworkRepositoryOutputPort
import ua.marchenko.artauction.domainservice.artwork.domain.random
import ua.marchenko.internal.NatsSubject
import ua.marchenko.commonmodels.Error
import ua.marchenko.artauction.domainservice.artwork.domain.Artwork
import ua.marchenko.artauction.domainservice.artwork.infrastructure.nats.mapper.toArtworkProto
import ua.marchenko.artauction.domainservice.utils.AbstractBaseIntegrationTest
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkByIdRequest
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkByIdResponse

class GetArtworkByIdNatsControllerTest : AbstractBaseIntegrationTest {

    @Autowired
    @Qualifier("mongoArtworkRepository")
    private lateinit var artworkRepository: ArtworkRepositoryOutputPort

    @Autowired
    private lateinit var natsPublisher: NatsMessagePublisher

    @Test
    fun `should return FindArtworkByIdResponse Success when artwork with this id exists`() {
        // GIVEN
        val artwork = artworkRepository.save(Artwork.random(id = null)).block()!!
        val request = FindArtworkByIdRequest.newBuilder().setId(artwork.id!!).build()
        val expectedResponse = FindArtworkByIdResponse.newBuilder().also { builder ->
            builder.successBuilder.setArtwork(artwork.toArtworkProto())
        }.build()

        // WHEN
        val result = natsPublisher.request(
            subject = NatsSubject.Artwork.FIND_BY_ID,
            payload = request,
            parser = FindArtworkByIdResponse.parser()
        )

        // THEN
        result.test()
            .expectNext(expectedResponse)
            .verifyComplete()
    }

    @Test
    fun `should return FindArtworkByIdResponse Failure when there is no artwork with this id`() {
        // GIVEN
        val id = getRandomString()
        val request = FindArtworkByIdRequest.newBuilder().setId(id).build()
        val expectedResponse = FindArtworkByIdResponse.newBuilder().also { builder ->
            builder.failureBuilder.setMessage(ERROR_MESSAGE_TEMPLATE.format(id))
            builder.failureBuilder.setNotFoundById(Error.getDefaultInstance())
        }.build()

        // WHEN
        val result = natsPublisher.request(
            subject = NatsSubject.Artwork.FIND_BY_ID,
            payload = request,
            parser = FindArtworkByIdResponse.parser()
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
