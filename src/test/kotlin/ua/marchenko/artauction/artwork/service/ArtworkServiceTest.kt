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
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class ArtworkServiceTest {

    private val mockArtworkRepository = mock(ArtworkRepository::class.java)
    private val mockUserService: UserService = mock(UserService::class.java)
    private val mockAuthentication: Authentication = mock()
    private val mockSecurityContext: SecurityContext = mock()

    private val artworkService: ArtworkService = ArtworkServiceImpl(mockArtworkRepository, mockUserService)

    @Test
    fun `findAll should return a list of artworks if there are present`() {
        val artworks = listOf(getRandomArtwork())
        `when`(mockArtworkRepository.findAll()).thenReturn(artworks)
        val result = artworkService.getAll()
        assertEquals(1, result.size)
        assertEquals(artworks[0].title, result[0].title)
    }

    @Test
    fun `findAll should return an empty list of artworks if there are no artworks`() {
        `when`(mockArtworkRepository.findAll()).thenReturn(listOf<Artwork>())
        val result = artworkService.getAll()
        assertEquals(0, result.size)
    }

    @Test
    fun `findById should return artwork by id if artwork with this id exists`() {
        val id = "1"
        val artwork = getRandomArtwork(id = id)
        `when`(mockArtworkRepository.findById(id)).thenReturn(artwork)
        val result = artworkService.getById(id)
        assertEquals(artwork, result)
    }

    @Test
    fun `findById should throw ArtworkNotFoundException if there is no artwork with this id`() {
        val id = "1"
        `when`(mockArtworkRepository.findById(id)).thenReturn(null)
        assertThrows<ArtworkNotFoundException> { artworkService.getById(id) }
    }

    @Test
    fun `save should set status and artist before calling repository method`() {
        val email = "test@example.com"
        val user = getRandomUser(email = email, role = Role.ARTIST)
        SecurityContextHolder.setContext(mockSecurityContext)
        `when`(mockSecurityContext.authentication).thenReturn(mockAuthentication)
        `when`(mockAuthentication.name).thenReturn(email)
        `when`(mockUserService.getByEmail(email)).thenReturn(user)
        val artwork = getRandomArtwork(status = null, artist = null)
        artworkService.save(artwork)
        verify(mockArtworkRepository).save(artwork.copy(status = ArtworkStatus.VIEW, artist = user))
    }

    @Test
    fun `existsById should return false if there are no artworks`() {
        val id = "1"
        `when`(mockArtworkRepository.existsById(id)).thenReturn(false)
        val result = artworkService.existsById(id)
        assertFalse(result)
    }

    @Test
    fun `existsById should return true if artwork with this id exists`() {
        val id = "1"
        `when`(mockArtworkRepository.existsById(id)).thenReturn(true)
        val result = artworkService.existsById(id)
        assertTrue(result)
    }

}
