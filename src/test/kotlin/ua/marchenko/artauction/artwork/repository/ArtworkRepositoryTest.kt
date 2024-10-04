//package ua.marchenko.artauction.artwork.repository
//
//import artwork.random
//import kotlin.test.Test
//import kotlin.test.assertFalse
//import kotlin.test.assertNull
//import org.bson.types.ObjectId
//import org.junit.jupiter.api.Assertions.assertEquals
//import org.junit.jupiter.api.Assertions.assertTrue
//import org.springframework.beans.factory.annotation.Autowired
//import ua.marchenko.artauction.artwork.model.MongoArtwork
//import ua.marchenko.artauction.common.AbstractBaseIntegrationTest
//import ua.marchenko.artauction.user.model.MongoUser
//import ua.marchenko.artauction.user.repository.UserRepository
//import user.random
//
//class ArtworkRepositoryTest : AbstractBaseIntegrationTest {
//
//    @Autowired
//    private lateinit var artworkRepository: ArtworkRepository
//
//    @Autowired
//    private lateinit var userRepository: UserRepository
//
//    @Test
//    fun `should save artwork`() {
//        // GIVEN
//        val artwork = MongoArtwork.random(id = null)
//
//        // WHEN
//        val savedArtwork = artworkRepository.save(artwork)
//
//        // THEN
//        assertEquals(artwork.copy(id = savedArtwork.id), savedArtwork)
//    }
//
//    @Test
//    fun `should find artwork by id when artwork with this id exists`() {
//        // GIVEN
//        val savedArtwork = artworkRepository.save(MongoArtwork.random(id = null))
//
//        // WHEN
//        val result = artworkRepository.findById(savedArtwork.id.toString())
//
//        // THEN
//        assertEquals(savedArtwork.id, result?.id)
//    }
//
//    @Test
//    fun `should return null when there is no artwork with this id`() {
//        // WHEN
//        val result = artworkRepository.findById(ObjectId().toString()) //random id
//
//        // THEN
//        assertNull(result, "Found artwork must be null")
//    }
//
//    @Test
//    fun `should return artwork with artist when artwork with this id exists`() {
//        // GIVEN
//        val savedArtist = userRepository.save(MongoUser.random(id = null))
//        val savedArtwork = artworkRepository.save(
//        MongoArtwork.random(id = null, artistId = savedArtist.id.toString()))
//
//        // WHEN
//        val result = artworkRepository.findFullById(savedArtwork.id.toString())
//
//        // THEN
//        assertEquals(savedArtist.name, result?.artist?.name)
//    }
//
//    @Test
//    fun `should return null when artist of artwork doesnt exist in db`() {
//        // GIVEN
//        val savedArtwork = artworkRepository.save(MongoArtwork.random(id = null))
//
//        // WHEN
//        val result = artworkRepository.findFullById(savedArtwork.id.toString())
//
//        // THEN
//        assertNull(result, "Found artwork must be null")
//    }
//
//    @Test
//    fun `should return all artworks with artist when they are exists`() {
//        // GIVEN
//        val savedArtist = userRepository.save(MongoUser.random(id = null))
//        val artworks = listOf(
//            MongoArtwork.random(artistId = savedArtist.id.toString()),
//            MongoArtwork.random(artistId = savedArtist.id.toString())
//        )
//        artworks.forEach { artwork -> artworkRepository.save(artwork) }
//
//        // WHEN
//        val result = artworkRepository.findFullAll()
//
//        // THEN
//        assertTrue(result.size >= artworks.size, "Size of list must be at least ${result.size}")
//        artworks.forEach { artwork ->
//            assertTrue(
//                result.any { it.title == artwork.title && it.artist?.name == savedArtist.name },
//                "Artwork with title ${artwork.title} and artist ${savedArtist.name} must be found"
//            )
//        }
//    }
//
//    @Test
//    fun `should return all artworks when they are exists`() {
//        // GIVEN
//        val artworks = listOf(MongoArtwork.random(id = null), MongoArtwork.random(id = null))
//        artworks.forEach { artwork -> artworkRepository.save(artwork) }
//
//        // WHEN
//        val result = artworkRepository.findAll()
//
//        // THEN
//        assertTrue(result.size >= artworks.size, "Size of list must be at least ${result.size}")
//        artworks.forEach { artwork ->
//            assertTrue(result.any { it.title == artwork.title }, "Artwork with title ${artwork.title} must be found")
//        }
//    }
//
//    @Test
//    fun `should return true when artwork with this id exists`() {
//        // GIVEN
//        val savedArtwork = artworkRepository.save(MongoArtwork.random(id = null))
//
//        // WHEN
//        val result = artworkRepository.existsById(savedArtwork.id.toString())
//
//        // THEN
//        assertTrue(result, "Artwork with given id must exist")
//    }
//
//    @Test
//    fun `should return false when there is no artwork with this id`() {
//        // WHEN
//        val result = artworkRepository.existsById(ObjectId().toString())
//
//        // THEN
//        assertFalse(result, "Artwork with given id must not exist")
//    }
//}
