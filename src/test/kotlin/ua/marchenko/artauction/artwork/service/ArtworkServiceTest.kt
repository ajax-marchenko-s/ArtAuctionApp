package ua.marchenko.artauction.artwork.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import ua.marchenko.artauction.artwork.enums.ArtworkStatus
import ua.marchenko.artauction.artwork.exception.ArtworkNotFoundException
import ua.marchenko.artauction.artwork.model.Artwork
import ua.marchenko.artauction.artwork.repository.ArtworkRepository
import ua.marchenko.artauction.common.artwork.getRandomArtwork
import ua.marchenko.artauction.common.user.getRandomUser
import ua.marchenko.artauction.user.enums.Role
import ua.marchenko.artauction.user.service.UserService
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import ua.marchenko.artauction.common.getRandomEmail
import ua.marchenko.artauction.common.getRandomString

class ArtworkServiceTest {

    private val mockArtworkRepository = mock(ArtworkRepository::class.java)
    private val mockUserService: UserService = mock(UserService::class.java)
    private val mockAuthentication: Authentication = mock()
    private val mockSecurityContext: SecurityContext = mock()

    private val artworkService: ArtworkService = ArtworkServiceImpl(mockArtworkRepository, mockUserService)

    @Test
    fun `getAll should return a list of artworks if there are present`() {
        // GIVEN
        val artworks = listOf(getRandomArtwork())
        `when`(mockArtworkRepository.findAll()).thenReturn(artworks)

        //WHEN
        val result = artworkService.getAll()

        //THEN
        assertEquals(1, result.size)
        assertEquals(artworks[0].title, result[0].title)
    }

    @Test
    fun `getAll should return an empty list of artworks if there are no artworks`() {
        // GIVEN
        `when`(mockArtworkRepository.findAll()).thenReturn(listOf<Artwork>())

        //WHEN
        val result = artworkService.getAll()

        //THEN
        assertEquals(0, result.size)
    }

    @Test
    fun `getById should return artwork by id if artwork with this id exists`() {
        // GIVEN
        val id = getRandomString()
        val artwork = getRandomArtwork(id = id)

        `when`(mockArtworkRepository.findById(id)).thenReturn(artwork)

        //WHEN
        val result = artworkService.getById(id)

        //THEN
        assertEquals(artwork, result)
    }

    @Test
    fun `getById should throw ArtworkNotFoundException if there is no artwork with this id`() {
        //GIVEN
        val id = getRandomString()
        `when`(mockArtworkRepository.findById(id)).thenReturn(null)

        //WHEN-THEN
        assertThrows<ArtworkNotFoundException> { artworkService.getById(id) }
    }

    @Test
    fun `save should set status and artist before calling repository method`() {
        //GIVEN
        val email = getRandomEmail()
        val user = getRandomUser(email = email, role = Role.ARTIST)
        val artworkToSave = getRandomArtwork(status = null, artist = null)

        SecurityContextHolder.setContext(mockSecurityContext)
        `when`(mockSecurityContext.authentication).thenReturn(mockAuthentication)
        `when`(mockAuthentication.name).thenReturn(email)
        `when`(mockUserService.getByEmail(email)).thenReturn(user)
        `when`(mockArtworkRepository.save(artworkToSave.copy(status = ArtworkStatus.VIEW, artist = user))).thenReturn(
            artworkToSave.copy(status = ArtworkStatus.VIEW, artist = user)
        )

        //WHEN
        val result = artworkService.save(artworkToSave)

        //THEN
        assertEquals(artworkToSave.copy(status = ArtworkStatus.VIEW, artist = user), result)
    }

    @Test
    fun `existsById should return false if there are no artworks`() {
        //GIVEN
        val id = getRandomString()
        `when`(mockArtworkRepository.existsById(id)).thenReturn(false)

        //WHEN
        val result = artworkService.existsById(id)

        //THEN
        assertFalse(result)
    }

    @Test
    fun `existsById should return true if artwork with this id exists`() {
        //GIVEN
        val id = getRandomString()
        `when`(mockArtworkRepository.existsById(id)).thenReturn(true)

        //WHEN
        val result = artworkService.existsById(id)

        //THEN
        assertTrue(result)
    }
}
