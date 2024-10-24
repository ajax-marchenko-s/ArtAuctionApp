package ua.marchenko.artauction.artwork.mapper

import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksFullResponse
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksResponse

class ArtworkMapperProtoTest {

    @Test
    fun `should build FindAllArtworksResponse with failure when called on throwable`() {
        // GIVEN
        val error = Throwable(ERROR_MESSAGE)
        val expectedResponse = FindAllArtworksResponse.newBuilder().also { builder ->
            builder.failureBuilder.message = ERROR_MESSAGE
        }.build()

        // WHEN
        val result = error.toFindAllArtworksFailureResponseProto()

        //THEN
        assertEquals(expectedResponse, result)
    }

    @Test
    fun `should build FindAllArtworksFullResponse with failure when called on throwable`() {
        // GIVEN
        val error = Throwable(ERROR_MESSAGE)
        val expectedResponse = FindAllArtworksFullResponse.newBuilder().also { builder ->
            builder.failureBuilder.message = ERROR_MESSAGE
        }.build()

        // WHEN
        val result = error.toFindAllArtworksFullFailureResponseProto()

        //THEN
        assertEquals(expectedResponse, result)
    }

    companion object {
        private const val ERROR_MESSAGE = "error message"
    }
}
