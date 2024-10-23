package ua.marchenko.artauction.artwork.mapper

import artwork.random
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ua.marchenko.artauction.artwork.model.MongoArtwork
import ua.marchenko.core.artwork.dto.ArtworkResponse

class ArtworkMapperProtoTest {

    @Test
    fun `should return CreateArtworkRequest`() {
        // GIVEN
        val mongoArtwork = MongoArtwork.random(artistId = ObjectId().toHexString())
        val expectedArtwork = ArtworkResponse(
            mongoArtwork.id!!.toHexString(),
            mongoArtwork.title!!,
            mongoArtwork.description!!,
            mongoArtwork.style!!,
            mongoArtwork.width!!,
            mongoArtwork.height!!,
            mongoArtwork.status!!,
            mongoArtwork.artistId!!.toHexString()
        )

        //WHEN
        val result = mongoArtwork.toResponse()

        //THEN
        assertEquals(expectedArtwork, result)
    }
}
