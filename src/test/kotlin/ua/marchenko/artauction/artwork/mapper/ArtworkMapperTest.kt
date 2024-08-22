package ua.marchenko.artauction.artwork.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import ua.marchenko.artauction.artwork.controller.dto.ArtworkResponse
import ua.marchenko.artauction.artwork.model.Artwork
import artwork.random
import ua.marchenko.artauction.user.enums.Role
import ua.marchenko.artauction.user.mapper.toUserResponse
import kotlin.test.Test
import ua.marchenko.artauction.artwork.controller.dto.CreateArtworkRequest
import ua.marchenko.artauction.artwork.enums.ArtworkStatus
import ua.marchenko.artauction.user.model.User
import user.random

class ArtworkMapperTest {

    @Test
    fun `ArtworkToArtworkResponse should return ArtworkResponse if Artwork has not null properties (except fields from business logic)`() {
        // GIVEN
        val artwork = Artwork.random(artist = User.random(role = Role.ARTIST))
        val expectedArtwork = ArtworkResponse(
            artwork.id!!.toString(),
            artwork.title!!,
            artwork.description!!,
            artwork.style!!,
            artwork.width!!,
            artwork.height!!,
            artwork.status!!,
            artwork.artist!!.toUserResponse()
        )

        //WHEN
        val result = artwork.toArtworkResponse()

        //THEN
        assertEquals(expectedArtwork, result)
    }

    @Test
    fun `ArtworkToArtworkResponse should set default values if Artwork has null properties (except fields from bl)`() {
        // GIVEN
        val artwork = Artwork.random(status = null)
        val expectedArtwork = ArtworkResponse(
            artwork.id!!.toString(),
            artwork.title!!,
            artwork.description!!,
            artwork.style!!,
            artwork.width!!,
            artwork.height!!,
            ArtworkStatus.UNKNOWN,
            artwork.artist!!.toUserResponse()
        )

        //WHEN
        val result = artwork.toArtworkResponse()

        //THEN
        assertEquals(expectedArtwork, result)
    }

    @Test
    fun `ArtworkRequestToArtwork should return Artwork`() {
        // GIVEN
        val artwork = CreateArtworkRequest.random()
        val expectedArtwork =
            Artwork(null, artwork.title, artwork.description, artwork.style, artwork.width, artwork.height, null, null)

        //WHEN
        val result = artwork.toArtwork()

        //THEN
        assertEquals(expectedArtwork, result)
    }
}
