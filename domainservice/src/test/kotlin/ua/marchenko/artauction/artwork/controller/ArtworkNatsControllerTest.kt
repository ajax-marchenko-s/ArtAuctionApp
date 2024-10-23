package ua.marchenko.artauction.artwork.controller

import artwork.ArtworkProtoFixture
import artwork.random
import artwork.toFullArtwork
import com.google.protobuf.GeneratedMessage
import io.nats.client.Connection
import com.google.protobuf.Parser
import getRandomString
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import ua.marchenko.artauction.artwork.mapper.toArtworkFullProto
import ua.marchenko.artauction.artwork.mapper.toArtworkProto
import ua.marchenko.artauction.artwork.mapper.toCreateArtworkFailureResponseProto
import ua.marchenko.artauction.artwork.mapper.toFullResponse
import ua.marchenko.artauction.artwork.mapper.toResponse
import ua.marchenko.artauction.artwork.model.MongoArtwork
import ua.marchenko.artauction.artwork.repository.ArtworkRepository
import ua.marchenko.artauction.common.AbstractBaseIntegrationTest
import ua.marchenko.artauction.user.model.MongoUser
import ua.marchenko.artauction.user.repository.UserRepository
import ua.marchenko.core.user.enums.Role
import ua.marchenko.core.user.exception.UserNotFoundException
import ua.marchenko.internal.NatsSubject
import ua.marchenko.internal.commonmodels.Error
import user.random
import ua.marchenko.internal.commonmodels.artwork.ArtworkStatus as ArtworkStatusProto
import ua.marchenko.internal.commonmodels.artwork.Artwork as ArtworkProto
import ua.marchenko.internal.input.reqreply.artwork.CreateArtworkResponse as CreateArtworkResponseProto
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkByIdRequest as FindArtworkByIdRequestProto
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkByIdResponse as FindArtworkByIdResponseProto
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkFullByIdRequest as FindArtworkFullByIdRequestProto
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkFullByIdResponse as FindArtworkFullByIdResponseProto
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksFullRequest as FindAllArtworksFullRequestProto
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksFullResponse as FindAllArtworksFullResponseProto
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksRequest as FindAllArtworksRequestProto
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksResponse as FindAllArtworksResponseProto

class ArtworkNatsControllerTest : AbstractBaseIntegrationTest {

    @Autowired
    private lateinit var natsConnection: Connection

    @Autowired
    private lateinit var artworkRepository: ArtworkRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `should return FindArtworkByIdResponse Success when artwork with this id exists`() {
        // GIVEN
        val artwork = artworkRepository.save(MongoArtwork.random()).block()!!
        val request = FindArtworkByIdRequestProto.newBuilder().setId(artwork.id!!.toHexString()).build()
        val expectedResponse = FindArtworkByIdResponseProto.newBuilder()
            .also { builder -> builder.successBuilder.setArtwork(artwork.toResponse().toArtworkProto()) }.build()

        // WHEN
        val result = doRequest(
            subject = NatsSubject.ArtworkNatsSubject.FIND_BY_ID,
            request = request,
            parser = FindArtworkByIdResponseProto.parser()
        )

        // THEN
        assertEquals(expectedResponse, result)
    }

    @Test
    fun `should return FindArtworkByIdResponse Failure when there is no artwork with this id`() {
        // GIVEN
        val id = getRandomString()
        val request = FindArtworkByIdRequestProto.newBuilder().setId(id).build()
        val expectedResponse = FindArtworkByIdResponseProto.newBuilder()
            .also { builder ->
                builder.failureBuilder.setMessage(ERROR_MESSAGE_TEMPLATE.format(id))
                builder.failureBuilder.setNotFoundById(
                    Error.getDefaultInstance()
                )
            }.build()

        // WHEN
        val result = doRequest(
            subject = NatsSubject.ArtworkNatsSubject.FIND_BY_ID,
            request = request,
            parser = FindArtworkByIdResponseProto.parser()
        )

        // THEN
        assertEquals(expectedResponse, result)
    }

    @Test
    fun `should save new artwork and return ArtworkResponse with data from CreateArtworkRequest`() {
        // GIVEN
        val savedArtist = userRepository.save(MongoUser.random(id = null, role = Role.ARTIST)).block()
        val request = ArtworkProtoFixture.randomCreateArtworkRequestProto(artistId = savedArtist!!.id!!.toHexString())
        val expectedResponse = CreateArtworkResponseProto.newBuilder().also { builder ->
            builder.successBuilder.setArtwork(
                ArtworkProto.newBuilder()
                    .setTitle(request.title)
                    .setDescription(request.description)
                    .setStyle(request.style)
                    .setStatus(ArtworkStatusProto.VIEW)
                    .setWidth(request.width)
                    .setHeight(request.height)
                    .setArtistId(request.artistId)
            )
        }.build()

        // WHEN
        val result = doRequest(
            subject = NatsSubject.ArtworkNatsSubject.CREATE,
            request = request,
            parser = CreateArtworkResponseProto.parser()
        )

        // THEN
        assertEquals(
            expectedResponse.toBuilder().successBuilder.artworkBuilder.build(),
            result.toBuilder().successBuilder.artworkBuilder.clearId().build()
        )
    }

    @Test
    fun `should return CreateArtworkResponse Failure when there is no user with this id and role artist`() {
        // GIVEN
        val savedUser = userRepository.save(MongoUser.random(id = null, role = Role.BUYER)).block()
        val request = ArtworkProtoFixture.randomCreateArtworkRequestProto(artistId = savedUser!!.id!!.toHexString())
        val expectedResponse = UserNotFoundException(
            "ID" to savedUser.id!!.toHexString(),
            "ROLE" to Role.ARTIST.name
        ).toCreateArtworkFailureResponseProto()

        // WHEN
        val result = doRequest(
            subject = NatsSubject.ArtworkNatsSubject.CREATE,
            request = request,
            parser = CreateArtworkResponseProto.parser()
        )

        //THEN
        assertEquals(expectedResponse, result)
    }

    @Test
    fun `should return FindArtworkFullByIdResponse Success when artwork with this id exists`() {
        // GIVEN
        val savedArtist = userRepository.save(MongoUser.random(id = null)).block()
        val artwork = artworkRepository.save(
            MongoArtwork.random(id = null, artistId = savedArtist!!.id.toString())
        ).block()!!.toFullArtwork(savedArtist)
        val request = FindArtworkByIdRequestProto.newBuilder().setId(artwork.id!!.toHexString()).build()
        val expectedResponse = FindArtworkFullByIdResponseProto.newBuilder()
            .also { builder ->
                builder.successBuilder.setArtwork(artwork.toFullResponse().toArtworkFullProto())
            }.build()

        // WHEN
        val result = doRequest(
            subject = NatsSubject.ArtworkNatsSubject.FIND_BY_ID_FULL,
            request = request,
            parser = FindArtworkFullByIdResponseProto.parser()
        )

        // THEN
        assertEquals(expectedResponse, result)
    }

    @Test
    fun `should return FindArtworkFullByIdResponse Failure when there is no artwork with this id`() {
        // GIVEN
        val id = getRandomString()
        val request = FindArtworkFullByIdRequestProto.newBuilder().setId(id).build()
        val expectedResponse = FindArtworkFullByIdResponseProto.newBuilder()
            .also { builder ->
                builder.failureBuilder.setMessage(ERROR_MESSAGE_TEMPLATE.format(id))
                builder.failureBuilder.setNotFoundById(
                    Error.getDefaultInstance()
                )
            }.build()

        // WHEN
        val result = doRequest(
            subject = NatsSubject.ArtworkNatsSubject.FIND_BY_ID_FULL,
            request = request,
            parser = FindArtworkFullByIdResponseProto.parser()
        )

        // THEN
        assertEquals(expectedResponse, result)
    }

    @Test
    fun `should return all artworks when they are exists`() {
        // GIVEN
        val artworks = listOf(
            artworkRepository.save(MongoArtwork.random(id = null)).block()!!.toResponse().toArtworkProto(),
            artworkRepository.save(MongoArtwork.random(id = null)).block()!!.toResponse().toArtworkProto(),
        )
        val request = FindAllArtworksRequestProto.newBuilder().setPage(0).setLimit(100).build()

        // WHEN
        val result = doRequest(
            subject = NatsSubject.ArtworkNatsSubject.FIND_ALL,
            request = request,
            parser = FindAllArtworksResponseProto.parser()
        )

        // THEN
        val foundArtworks = result.success.artworksList
        assertTrue(foundArtworks.containsAll(artworks), "Artworks $artworks not found in returned list")
    }

    @Test
    fun `should return all full artworks when they are exists`() {
        // GIVEN
        val savedArtist = userRepository.save(MongoUser.random(id = null)).block()
        val artworks = listOf(
            artworkRepository.save(MongoArtwork.random(artistId = savedArtist!!.id.toString())).block()!!
                .toFullArtwork(savedArtist).toFullResponse().toArtworkFullProto(),
            artworkRepository.save(MongoArtwork.random(artistId = savedArtist.id.toString())).block()!!
                .toFullArtwork(savedArtist).toFullResponse().toArtworkFullProto(),
        )
        val request = FindAllArtworksFullRequestProto.newBuilder().setPage(0).setLimit(100).build()

        // WHEN
        val result = doRequest(
            subject = NatsSubject.ArtworkNatsSubject.FIND_ALL_FULL,
            request = request,
            parser = FindAllArtworksFullResponseProto.parser()
        )

        // THEN
        val foundArtworks = result.success.artworksList
        assertTrue(foundArtworks.containsAll(artworks), "Artworks $artworks not found in returned list")
    }

    private fun <RequestT : GeneratedMessage, ResponseT : GeneratedMessage> doRequest(
        subject: String,
        request: RequestT,
        parser: Parser<ResponseT>,
    ): ResponseT {
        val response = natsConnection.request(
            subject,
            request.toByteArray()
        )
        return parser.parseFrom(response.get().data)
    }

    companion object {
        private const val ERROR_MESSAGE_TEMPLATE = "Artwork with ID %s not found"
    }
}
