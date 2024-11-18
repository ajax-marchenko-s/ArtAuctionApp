package ua.marchenko.artauction.artwork.controller.nats

import ua.marchenko.artauction.artwork.random
import ua.marchenko.artauction.getRandomString
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import ua.marchenko.artauction.artwork.mapper.toArtworkProto
import ua.marchenko.artauction.artwork.model.MongoArtwork
import ua.marchenko.artauction.artwork.repository.ArtworkRepository
import ua.marchenko.artauction.common.AbstractBaseNatsControllerTest
import ua.marchenko.core.artwork.enums.ArtworkStatus
import ua.marchenko.internal.NatsSubject
import ua.marchenko.commonmodels.Error
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkByIdRequest
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkByIdResponse

class GetArtworkByIdNatsControllerTest : AbstractBaseNatsControllerTest() {

    @Autowired
    private lateinit var artworkRepository: ArtworkRepository

    @Test
    fun `should return FindArtworkByIdResponse Success when artwork with this id exists`() {
        // GIVEN
        val artwork = artworkRepository.save(MongoArtwork.random(status = ArtworkStatus.ON_AUCTION)).block()!!
        val request = FindArtworkByIdRequest.newBuilder().setId(artwork.id!!.toHexString()).build()
        val expectedResponse = FindArtworkByIdResponse.newBuilder().also { builder ->
            builder.successBuilder.setArtwork(artwork.toArtworkProto())
        }.build()

        // WHEN
        val result = doRequest(
            subject = NatsSubject.Artwork.FIND_BY_ID,
            request = request,
            parser = FindArtworkByIdResponse.parser()
        )

        // THEN
        assertEquals(expectedResponse, result)
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
        val result = doRequest(
            subject = NatsSubject.Artwork.FIND_BY_ID,
            request = request,
            parser = FindArtworkByIdResponse.parser()
        )

        // THEN
        assertEquals(expectedResponse, result)
    }

    companion object {
        private const val ERROR_MESSAGE_TEMPLATE = "Artwork with ID %s not found"
    }
}
