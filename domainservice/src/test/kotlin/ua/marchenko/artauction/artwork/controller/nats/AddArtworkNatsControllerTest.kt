package ua.marchenko.artauction.artwork.controller.nats

import artwork.ArtworkProtoFixture
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import ua.marchenko.artauction.artwork.mapper.toCreateArtworkFailureResponseProto
import ua.marchenko.artauction.common.AbstractBaseNatsControllerTest
import ua.marchenko.artauction.user.model.MongoUser
import ua.marchenko.artauction.user.repository.UserRepository
import ua.marchenko.core.user.enums.Role
import ua.marchenko.core.user.exception.UserNotFoundException
import ua.marchenko.internal.NatsSubject
import ua.marchenko.internal.commonmodels.artwork.Artwork
import ua.marchenko.internal.commonmodels.artwork.ArtworkStatus
import ua.marchenko.internal.commonmodels.artwork.ArtworkStyle
import ua.marchenko.internal.input.reqreply.artwork.CreateArtworkResponse
import user.random

class AddArtworkNatsControllerTest : AbstractBaseNatsControllerTest() {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `should save new artwork and return ArtworkResponse with data from CreateArtworkRequest`() {
        // GIVEN
        val savedArtist = userRepository.save(MongoUser.random(id = null, role = Role.ARTIST)).block()
        val request = ArtworkProtoFixture.randomCreateArtworkRequestProto(
            artistId = savedArtist!!.id!!.toHexString(),
            style = ArtworkStyle.ARTWORK_STYLE_ABSTRACT
        )
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
        val result = doRequest(
            subject = NatsSubject.ArtworkNatsSubject.CREATE,
            request = request,
            parser = CreateArtworkResponse.parser()
        )

        // THEN
        assertEquals(
            expectedResponse.success.artwork,
            result.toBuilder().successBuilder.artworkBuilder.clearId().build()
        )
    }

    @Test
    fun `should return CreateArtworkResponse Failure when there is no user with this id and role artist`() {
        // GIVEN
        val savedUser = userRepository.save(MongoUser.random(id = null, role = Role.BUYER)).block()
        val request = ArtworkProtoFixture.randomCreateArtworkRequestProto(
            artistId = savedUser!!.id!!.toHexString(),
            style = ArtworkStyle.ARTWORK_STYLE_RENAISSANCE
        )
        val expectedResponse = UserNotFoundException(
            "ID" to savedUser.id!!.toHexString(),
            "ROLE" to Role.ARTIST.name
        ).toCreateArtworkFailureResponseProto()

        // WHEN
        val result = doRequest(
            subject = NatsSubject.ArtworkNatsSubject.CREATE,
            request = request,
            parser = CreateArtworkResponse.parser()
        )

        // THEN
        assertEquals(expectedResponse, result)
    }
}
