package ua.marchenko.artauction.artwork.service

import artwork.random
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import ua.marchenko.artauction.artwork.enums.ArtworkStatus
import ua.marchenko.artauction.artwork.exception.ArtworkNotFoundException
import ua.marchenko.artauction.artwork.model.Artwork
import ua.marchenko.artauction.artwork.repository.ArtworkRepository
import ua.marchenko.artauction.user.enums.Role
import ua.marchenko.artauction.user.service.UserService
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.mockito.Mockito.mock
import getRandomEmail
import getRandomObjectId
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.whenever
import ua.marchenko.artauction.user.model.User
import user.random

class ArtworkServiceTest {

    private val mockArtworkRepository = mock(ArtworkRepository::class.java)
    private val mockUserService: UserService = mock(UserService::class.java)
    private val mockAuthentication: Authentication = mock()
    private val mockSecurityContext: SecurityContext = mock()

    private val artworkService: ArtworkService = ArtworkServiceImpl(mockArtworkRepository, mockUserService)

    @Test
    fun `getAll should return a list of artworks if there are present`() {
        // GIVEN
        val artworks = listOf(Artwork.random())
        whenever(mockArtworkRepository.findAll()) doReturn (artworks)

        //WHEN
        val result = artworkService.getAll()

        //THEN
        assertEquals(1, result.size)
        assertEquals(artworks[0].title, result[0].title)
    }

    @Test
    fun `getAll should return an empty list of artworks if there are no artworks`() {
        // GIVEN
        whenever(mockArtworkRepository.findAll()) doReturn (listOf())

        //WHEN
        val result = artworkService.getAll()

        //THEN
        assertEquals(0, result.size)
    }

    @Test
    fun `getById should return artwork by id if artwork with this id exists`() {
        // GIVEN
        val id = getRandomObjectId().toString()
        val artwork = Artwork.random(id = id)

        whenever(mockArtworkRepository.findById(id)) doReturn (artwork)

        //WHEN
        val result = artworkService.getById(id)

        //THEN
        assertEquals(artwork, result)
    }

    @Test
    fun `getById should throw ArtworkNotFoundException if there is no artwork with this id`() {
        //GIVEN
        val id = getRandomObjectId().toString()
        whenever(mockArtworkRepository.findById(id)) doReturn (null)

        //WHEN-THEN
        assertThrows<ArtworkNotFoundException> { artworkService.getById(id) }
    }

    @Test
    fun `save should set status and artist before calling repository method`() {
        //GIVEN
        val email = getRandomEmail()
        val user = User.random(email = email, role = Role.ARTIST)
        val artworkToSave = Artwork.random(status = null, artist = null)

        SecurityContextHolder.setContext(mockSecurityContext)
        whenever(mockSecurityContext.authentication) doReturn (mockAuthentication)
        whenever(mockAuthentication.name) doReturn (email)
        whenever(mockUserService.getByEmail(email)) doReturn (user)
        whenever(
            mockArtworkRepository.save(
                artworkToSave.copy(
                    status = ArtworkStatus.VIEW,
                    artist = user
                )
            )
        ) doReturn (artworkToSave.copy(status = ArtworkStatus.VIEW, artist = user))

        //WHEN
        val result = artworkService.save(artworkToSave)

        //THEN
        assertEquals(artworkToSave.copy(status = ArtworkStatus.VIEW, artist = user), result)
    }

    @Test
    fun `existsById should return false if there are no artworks`() {
        //GIVEN
        val id = getRandomObjectId().toString()
        whenever(mockArtworkRepository.existsById(id)) doReturn (false)

        //WHEN
        val result = artworkService.existsById(id)

        //THEN
        assertFalse(result)
    }

    @Test
    fun `existsById should return true if artwork with this id exists`() {
        //GIVEN
        val id = getRandomObjectId().toString()
        whenever(mockArtworkRepository.existsById(id)) doReturn (true)

        //WHEN
        val result = artworkService.existsById(id)

        //THEN
        assertTrue(result)
    }
}
