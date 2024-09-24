package ua.marchenko.artauction.artwork.service

import artwork.random
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import ua.marchenko.artauction.artwork.enums.ArtworkStatus
import ua.marchenko.artauction.artwork.exception.ArtworkNotFoundException
import ua.marchenko.artauction.artwork.model.MongoArtwork
import ua.marchenko.artauction.artwork.repository.ArtworkRepository
import ua.marchenko.artauction.user.enums.Role
import ua.marchenko.artauction.user.service.UserService
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import getRandomEmail
import getRandomObjectId
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import ua.marchenko.artauction.artwork.model.projection.ArtworkFull
import ua.marchenko.artauction.user.model.MongoUser
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

    @Test
    fun `should return a list of artworks when artworks are present`() {
        // GIVEN
        val artworks = listOf(MongoArtwork.random())
        every { mockArtworkRepository.findAll() } returns artworks

        //WHEN
        val result = artworkService.getAll()

        //THEN
        assertEquals(1, result.size)
        assertEquals(artworks[0].title, result[0].title)
    }

    @Test
    fun `should return an empty list of artworks when there are no artworks`() {
        // GIVEN
        every { mockArtworkRepository.findAll() } returns emptyList()

        //WHEN
        val result = artworkService.getAll()

        //THEN
        assertEquals(0, result.size)
    }

    @Test
    fun `should return artwork by id when artwork with this id exists`() {
        // GIVEN
        val id = getRandomObjectId().toHexString()
        val artwork = MongoArtwork.random(id = id)

        every { mockArtworkRepository.findById(id) } returns artwork

        //WHEN
        val result = artworkService.getById(id)

        //THEN
        assertEquals(artwork, result)
    }

    @Test
    fun `should throw ArtworkNotFoundException when there is no artwork with this id`() {
        //GIVEN
        val id = getRandomObjectId().toHexString()
        every { mockArtworkRepository.findById(id) } returns null

        //WHEN //THEN
        assertThrows<ArtworkNotFoundException> { artworkService.getById(id) }
    }

    @Test
    fun `should return full artwork by id when artwork with this id exists`() {
        // GIVEN
        val id = getRandomObjectId().toHexString()
        val artwork = ArtworkFull.random(id = id)

        every { mockArtworkRepository.findFullById(id) } returns artwork

        // WHEN
        val result = artworkService.getFullById(id)

        // THEN
        assertEquals(artwork, result)
    }

    @Test
    fun `should throw ArtworkNotFoundException when there is no full artwork with this id`() {
        //GIVEN
        val id = getRandomObjectId().toHexString()
        every { mockArtworkRepository.findFullById(id) } returns null

        //WHEN //THEN
        assertThrows<ArtworkNotFoundException> { artworkService.getFullById(id) }
    }

    @Test
    fun `should return a list of full artworks when artworks are present`() {
        // GIVEN
        val artworks = listOf(ArtworkFull.random())
        every { mockArtworkRepository.findFullAll() } returns artworks

        // WHEN
        val result = artworkService.getFullAll()

        // THEN
        assertEquals(1, result.size)
        assertEquals(artworks[0].title, result[0].title)
    }

    @Test
    fun `should set status and artist before calling repository method`() {
        //GIVEN
        val email = getRandomEmail()
        val user = MongoUser.random(email = email, role = Role.ARTIST)
        val artworkToSave = MongoArtwork.random(status = null, artistId = null)

        SecurityContextHolder.setContext(mockSecurityContext)
        every { mockSecurityContext.authentication } returns mockAuthentication
        every { mockAuthentication.name } returns email
        every { mockUserService.getByEmail(email) } returns user
        every {
            mockArtworkRepository.save(
                artworkToSave.copy(
                    status = ArtworkStatus.VIEW,
                    artistId = user.id
                )
            )
        } returns artworkToSave.copy(status = ArtworkStatus.VIEW, artistId = user.id)

        //WHEN
        val result = artworkService.save(artworkToSave)

        //THEN
        assertEquals(artworkToSave.copy(status = ArtworkStatus.VIEW, artistId = user.id), result)
    }

    @Test
    fun `should return false when there is no artwork with given id`() {
        //GIVEN
        val id = getRandomObjectId().toHexString()
        every { mockArtworkRepository.existsById(id) } returns false

        //WHEN
        val result = artworkService.existsById(id)

        //THEN
        assertFalse(result)
    }

    @Test
    fun `should return true when artwork with this id exists`() {
        //GIVEN
        val id = getRandomObjectId().toHexString()
        every { mockArtworkRepository.existsById(id) } returns true

        //WHEN
        val result = artworkService.existsById(id)

        //THEN
        assertTrue(result)
    }
}
