package ua.marchenko.artauction.artwork.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import ua.marchenko.artauction.artwork.controller.dto.ArtworkResponse
import ua.marchenko.artauction.artwork.enums.ArtworkStatus
import ua.marchenko.artauction.artwork.enums.ArtworkStyle
import ua.marchenko.artauction.artwork.model.Artwork
import ua.marchenko.artauction.common.artwork.getRandomArtwork
import ua.marchenko.artauction.common.artwork.getRandomArtworkRequest
import ua.marchenko.artauction.common.user.getRandomUser
import ua.marchenko.artauction.user.enums.Role
import ua.marchenko.artauction.user.mapper.toUserResponse
import kotlin.test.Test

class ArtworkMapperTest {

    @Test
    fun `ArtworkToArtworkResponse should return ArtworkResponse if Artwork has not null properties (except fields from business logic)`() {
        val artwork = getRandomArtwork(artist = getRandomUser(role = Role.ARTIST))
        val expectedArtwork = ArtworkResponse(
            artwork.id ?: "",
            artwork.title ?: "",
            artwork.description ?: "",
            artwork.style ?: ArtworkStyle.POP_ART,
            artwork.width ?: 0,
            artwork.height ?: 0,
            artwork.status ?: ArtworkStatus.VIEW,
            artwork.artist?.toUserResponse() ?: getRandomUser().toUserResponse()
        )
        val result = artwork.toArtworkResponse()
        assertEquals(expectedArtwork, result)
    }

    @Test
    fun `ArtworkToArtworkResponse should throwIllegalArgumentException if Artwork has null properties (except fields from bl)`() {
        val artwork = getRandomArtwork(artist = null)
        assertThrows<IllegalArgumentException> { artwork.toArtworkResponse() }
    }

    @Test
    fun `ArtworkRequestToArtwork should return Artwork`() {
        val artwork = getRandomArtworkRequest()
        val expectedArtwork =
            Artwork(null, artwork.title, artwork.description, artwork.style, artwork.width, artwork.height, null, null)
        val result = artwork.toArtwork()
        assertEquals(expectedArtwork, result)
    }

}
