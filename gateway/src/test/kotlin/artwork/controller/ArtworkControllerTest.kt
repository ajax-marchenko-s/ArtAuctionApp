package artwork.controller

import artwork.ArtworkProtoFixture.randomSuccessCreateArtworkResponseProto
import artwork.ArtworkProtoFixture.randomSuccessFindAllArtworkFullResponseProto
import artwork.ArtworkProtoFixture.randomSuccessFindAllArtworkResponseProto
import artwork.ArtworkProtoFixture.randomSuccessFindArtworkByIdResponseProto
import artwork.ArtworkProtoFixture.randomSuccessFindArtworkFullByIdResponseProto
import artwork.random
import getRandomString
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Test
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test
import ua.marchenko.core.artwork.enums.ArtworkStyle
import ua.marchenko.gateway.artwork.controller.ArtworkController
import ua.marchenko.gateway.artwork.controller.dto.CreateArtworkRequest
import ua.marchenko.gateway.artwork.mapper.toArtworkFullResponse
import ua.marchenko.gateway.artwork.mapper.toArtworkResponse
import ua.marchenko.gateway.artwork.mapper.toArtworksList
import ua.marchenko.gateway.artwork.mapper.toCreateArtworkRequestProto
import ua.marchenko.gateway.artwork.mapper.toFullArtworkList
import ua.marchenko.gateway.common.nats.NatsClient
import ua.marchenko.internal.NatsSubject
import ua.marchenko.internal.commonmodels.artwork.Artwork.ArtworkStatus as ArtworkStatusProto
import ua.marchenko.internal.commonmodels.artwork.Artwork.ArtworkStyle as ArtworkStyleProto
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksFullRequest as FindAllArtworksFullRequestProto
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksFullResponse as FindAllArtworksFullResponseProto
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksRequest as FindAllArtworksRequestProto
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksResponse as FindAllArtworksResponseProto
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkByIdRequest as FindArtworkByIdRequestProto
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkByIdResponse as FindArtworkByIdResponseProto
import ua.marchenko.internal.input.reqreply.artwork.CreateArtworkResponse as CreateArtworkResponseProto
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkFullByIdRequest as FindArtworkFullByIdRequestProto
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkFullByIdResponse as FindArtworkFullByIdResponseProto

class ArtworkControllerTest {

    @MockK
    private lateinit var natsClient: NatsClient

    @InjectMockKs
    private lateinit var artworkController: ArtworkController

    @Test
    fun `should return ArtworkResponse when create artwork success`() {
        // GIVEN
        val request = CreateArtworkRequest.random(style = ArtworkStyle.MINIMALISM)
        val response = randomSuccessCreateArtworkResponseProto()

        every {
            natsClient.doRequest(
                subject = NatsSubject.Artwork.CREATE,
                payload = request.toCreateArtworkRequestProto(),
                parser = CreateArtworkResponseProto.parser()
            )
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
            natsClient.doRequest(
                subject = NatsSubject.Artwork.FIND_BY_ID,
                payload = FindArtworkByIdRequestProto.newBuilder().setId(id).build(),
                parser = FindArtworkByIdResponseProto.parser()
            )
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
            natsClient.doRequest(
                subject = NatsSubject.Artwork.FIND_BY_ID_FULL,
                payload = FindArtworkFullByIdRequestProto.newBuilder().setId(id).build(),
                parser = FindArtworkFullByIdResponseProto.parser()
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
            natsClient.doRequest(
                subject = NatsSubject.Artwork.FIND_ALL,
                payload = FindAllArtworksRequestProto.newBuilder().setPage(0).setLimit(10).build(),
                parser = FindAllArtworksResponseProto.parser()
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
            natsClient.doRequest(
                subject = NatsSubject.Artwork.FIND_ALL_FULL,
                payload = FindAllArtworksFullRequestProto.newBuilder().setPage(0).setLimit(10).build(),
                parser = FindAllArtworksFullResponseProto.parser()
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
