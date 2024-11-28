package infrastructure.rest.controller

import ua.marchenko.artauction.infrastructure.artwork.ArtworkProtoFixture.randomSuccessCreateArtworkResponseProto
import ua.marchenko.artauction.infrastructure.artwork.ArtworkProtoFixture.randomSuccessFindAllArtworkFullResponseProto
import ua.marchenko.artauction.infrastructure.artwork.ArtworkProtoFixture.randomSuccessFindAllArtworkResponseProto
import ua.marchenko.artauction.infrastructure.artwork.ArtworkProtoFixture.randomSuccessFindArtworkByIdResponseProto
import ua.marchenko.artauction.infrastructure.artwork.ArtworkProtoFixture.randomSuccessFindArtworkFullByIdResponseProto
import ua.marchenko.artauction.infrastructure.artwork.random
import ua.marchenko.artauction.getRandomString
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Test
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test
import ua.marchenko.artauction.gateway.application.port.output.ArtworkMessageHandlerOutputPort
import ua.marchenko.artauction.gateway.infrastructure.rest.ArtworkController
import ua.marchenko.artauction.gateway.infrastructure.rest.dto.CreateArtworkRequest
import ua.marchenko.artauction.gateway.infrastructure.rest.mapper.toArtworkFullResponse
import ua.marchenko.artauction.gateway.infrastructure.rest.mapper.toArtworkResponse
import ua.marchenko.artauction.gateway.infrastructure.rest.mapper.toArtworksList
import ua.marchenko.artauction.gateway.infrastructure.rest.mapper.toCreateArtworkRequestProto
import ua.marchenko.artauction.gateway.infrastructure.rest.mapper.toFullArtworkList
import ua.marchenko.internal.commonmodels.artwork.Artwork.ArtworkStatus as ArtworkStatusProto
import ua.marchenko.internal.commonmodels.artwork.Artwork.ArtworkStyle as ArtworkStyleProto
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksFullRequest as FindAllArtworksFullRequestProto
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksRequest as FindAllArtworksRequestProto
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkByIdRequest as FindArtworkByIdRequestProto
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkFullByIdRequest as FindArtworkFullByIdRequestProto

class ArtworkControllerTest {

    @MockK
    private lateinit var artworkMessageHandlerOutputPort: ArtworkMessageHandlerOutputPort

    @InjectMockKs
    private lateinit var artworkController: ArtworkController

    @Test
    fun `should return ArtworkResponse when create artwork success`() {
        // GIVEN
        val request = CreateArtworkRequest.random()
        val response = randomSuccessCreateArtworkResponseProto()

        every {
            artworkMessageHandlerOutputPort.createArtwork(request.toCreateArtworkRequestProto())
        } returns response.toMono()

        // WHEN
        val result = artworkController.addArtwork(request)

        // THEN
        result.test()
            .expectNext(response.toArtworkResponse())
            .verifyComplete()
    }

    @Test
    fun `should return ArtworkResponse when artwork with this id exists`() {
        // GIVEN
        val id = getRandomString()
        val response = randomSuccessFindArtworkByIdResponseProto(
            style = ArtworkStyleProto.ARTWORK_STYLE_ABSTRACT,
            status = ArtworkStatusProto.ARTWORK_STATUS_ON_AUCTION
        )
        every {
            artworkMessageHandlerOutputPort.getArtworkById(FindArtworkByIdRequestProto.newBuilder().setId(id).build())
        } returns response.toMono()

        // WHEN
        val result = artworkController.getArtworkById(id)

        //THEN
        result.test()
            .expectNext(response.toArtworkResponse())
            .verifyComplete()
    }

    @Test
    fun `should return ArtworkFullResponse when artwork with this id exists`() {
        // GIVEN
        val id = getRandomString()
        val response = randomSuccessFindArtworkFullByIdResponseProto(
            style = ArtworkStyleProto.ARTWORK_STYLE_CUBISM,
            status = ArtworkStatusProto.ARTWORK_STATUS_SOLD
        )
        every {
            artworkMessageHandlerOutputPort.getFullArtworkById(
                FindArtworkFullByIdRequestProto.newBuilder().setId(id).build()
            )
        } returns response.toMono()

        // WHEN
        val result = artworkController.getFullArtworkById(id)

        //THEN
        result.test()
            .expectNext(response.toArtworkFullResponse())
            .verifyComplete()
    }

    @Test
    fun `should return list of ArtworkResponse when there are some artworks`() {
        // GIVEN
        val response = randomSuccessFindAllArtworkResponseProto()
        every {
            artworkMessageHandlerOutputPort.getAllArtworks(
                FindAllArtworksRequestProto.newBuilder().setPage(0).setLimit(10).build()
            )
        } returns response.toMono()

        // WHEN
        val result = artworkController.getAllArtworks(page = 0, limit = 10)

        //THEN
        result.test()
            .expectNext(response.toArtworksList())
            .verifyComplete()
    }

    @Test
    fun `should return list of ArtworkFullResponse when there are some artworks`() {
        // GIVEN
        val response = randomSuccessFindAllArtworkFullResponseProto()
        every {
            artworkMessageHandlerOutputPort.getAllFullArtworks(
                FindAllArtworksFullRequestProto.newBuilder().setPage(0).setLimit(10).build()
            )
        } returns response.toMono()

        // WHEN
        val result = artworkController.getAllFullArtworks(page = 0, limit = 10)

        //THEN
        result.test()
            .expectNext(response.toFullArtworkList())
            .verifyComplete()
    }
}
