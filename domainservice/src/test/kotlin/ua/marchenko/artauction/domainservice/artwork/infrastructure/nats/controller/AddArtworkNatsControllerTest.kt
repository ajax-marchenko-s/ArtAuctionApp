package ua.marchenko.artauction.domainservice.artwork.infrastructure.nats.controller

import kotlin.test.assertEquals
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.kotlin.test.test
import systems.ajax.nats.publisher.api.NatsMessagePublisher
import ua.marchenko.artauction.domainservice.artwork.infrastructure.ArtworkProtoFixture
import ua.marchenko.artauction.domainservice.artwork.infrastructure.nats.mapper.toCreateArtworkFailureResponseProto
import ua.marchenko.artauction.domainservice.user.infrastructure.mongo.repository.MongoUserRepository
import ua.marchenko.artauction.core.user.exception.UserNotFoundException
import ua.marchenko.artauction.domainservice.user.domain.CreateUser
import ua.marchenko.artauction.domainservice.utils.AbstractBaseIntegrationTest
import ua.marchenko.artauction.domainservice.user.domain.random
import ua.marchenko.internal.NatsSubject
import ua.marchenko.internal.commonmodels.artwork.Artwork
import ua.marchenko.internal.commonmodels.artwork.Artwork.ArtworkStatus
import ua.marchenko.internal.commonmodels.artwork.Artwork.ArtworkStyle
import ua.marchenko.internal.input.reqreply.artwork.CreateArtworkResponse

class AddArtworkNatsControllerTest : AbstractBaseIntegrationTest {

    @Autowired
    private lateinit var userRepository: MongoUserRepository

    @Autowired
    private lateinit var natsPublisher: NatsMessagePublisher

    @Test
    fun `should save new artwork and return ArtworkResponse with data from CreateArtworkRequest`() {
        // GIVEN
        val savedArtist = userRepository.save(CreateUser.random()).block()
        val request = ArtworkProtoFixture.randomCreateArtworkRequestProto(artistId = savedArtist!!.id)
        val expectedResponse = CreateArtworkResponse.newBuilder().also { builder ->
            builder.successBuilder.setArtwork(
                Artwork.newBuilder().also {
                    it.title = request.title
                    it.description = request.description
                    it.style = request.style
                    it.status = ArtworkStatus.ARTWORK_STATUS_VIEW
                    it.width = request.width
                    it.height = request.height
                    it.artistId = request.artistId
                })
        }.build()

        // WHEN
        val result = natsPublisher.request(
            subject = NatsSubject.Artwork.CREATE,
            payload = request,
            parser = CreateArtworkResponse.parser()
        )

        // THEN
        result.test()
            .assertNext {
                assertEquals(
                    expectedResponse.success.artwork,
                    it.toBuilder().successBuilder.artworkBuilder.clearId().build()
                )
            }.verifyComplete()
    }

    @Test
    fun `should return CreateArtworkResponse Failure when there is no user with this id`() {
        // GIVEN
        val artistId = ObjectId().toHexString()
        val request = ArtworkProtoFixture.randomCreateArtworkRequestProto(
            artistId = artistId,
            style = ArtworkStyle.ARTWORK_STYLE_RENAISSANCE
        )
        val expectedResponse =
            UserNotFoundException(value = artistId).toCreateArtworkFailureResponseProto()

        // WHEN
        val result = natsPublisher.request(
            subject = NatsSubject.Artwork.CREATE,
            payload = request,
            parser = CreateArtworkResponse.parser()
        )

        // THEN
        result.test()
            .expectNext(expectedResponse)
            .verifyComplete()
    }
}
