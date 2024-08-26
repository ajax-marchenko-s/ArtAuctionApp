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
import getRandomEmail
import getRandomObjectId
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.BeforeEach
import ua.marchenko.artauction.user.model.User
import user.random

class ArtworkServiceTest {

    @MockK
    private lateinit var mockArtworkRepository: ArtworkRepository

    @MockK
    private lateinit var mockUserService: UserService

    @MockK
    private lateinit var mockAuthentication: Authentication

    @MockK
    private lateinit var mockSecurityContext: SecurityContext

    @InjectMockKs
    private lateinit var artworkService: ArtworkServiceImpl

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `should return a list of artworks when artworks are present`() {
        // GIVEN
        val artworks = listOf(Artwork.random())
        every { mockArtworkRepository.findAll() } returns (artworks)

        //WHEN
        val result = artworkService.getAll()

        //THEN
        assertEquals(1, result.size)
        assertEquals(artworks[0].title, result[0].title)
    }

    @Test
    fun `should return an empty list of artworks when there are no artworks`() {
        // GIVEN
        every { mockArtworkRepository.findAll() } returns (listOf())

        //WHEN
        val result = artworkService.getAll()

        //THEN
        assertEquals(0, result.size)
    }

    @Test
    fun `should return artwork by id when artwork with this id exists`() {
        // GIVEN
        val id = getRandomObjectId().toString()
        val artwork = Artwork.random(id = id)

        every { mockArtworkRepository.findById(id) } returns (artwork)

        //WHEN
        val result = artworkService.getById(id)

        //THEN
        assertEquals(artwork, result)
    }

    @Test
    fun `should throw ArtworkNotFoundException when there is no artwork with this id`() {
        //GIVEN
        val id = getRandomObjectId().toString()
        every { mockArtworkRepository.findById(id) } returns (null)

        //WHEN //THEN
        assertThrows<ArtworkNotFoundException> { artworkService.getById(id) }
    }

    @Test
    fun `should set status and artist before calling repository method`() {
        //GIVEN
        val email = getRandomEmail()
        val user = User.random(email = email, role = Role.ARTIST)
        val artworkToSave = Artwork.random(status = null, artist = null)

        SecurityContextHolder.setContext(mockSecurityContext)
        every { mockSecurityContext.authentication } returns (mockAuthentication)
        every { mockAuthentication.name } returns (email)
        every { mockUserService.getByEmail(email) } returns (user)
        every {
            mockArtworkRepository.save(
                artworkToSave.copy(
                    status = ArtworkStatus.VIEW,
                    artist = user
                )
            )
        } returns (artworkToSave.copy(status = ArtworkStatus.VIEW, artist = user))

        //WHEN
        val result = artworkService.save(artworkToSave)

        //THEN
        assertEquals(artworkToSave.copy(status = ArtworkStatus.VIEW, artist = user), result)
    }

    @Test
    fun `should return false when there is no artwork with given id`() {
        //GIVEN
        val id = getRandomObjectId().toString()
        every { mockArtworkRepository.existsById(id) } returns (false)

        //WHEN
        val result = artworkService.existsById(id)

        //THEN
        assertFalse(result)
    }

    @Test
    fun `should return true when artwork with this id exists`() {
        //GIVEN
        val id = getRandomObjectId().toString()
        every { mockArtworkRepository.existsById(id) } returns (true)

        //WHEN
        val result = artworkService.existsById(id)

        //THEN
        assertTrue(result)
    }
}
