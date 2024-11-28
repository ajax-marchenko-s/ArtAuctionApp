package ua.marchenko.artauction.domainservice.artwork.infrastructure

import kotlin.random.Random
import org.bson.types.ObjectId
import ua.marchenko.artauction.domainservice.artwork.getRandomString
import ua.marchenko.internal.commonmodels.artwork.Artwork.ArtworkStyle
import ua.marchenko.internal.input.reqreply.artwork.CreateArtworkRequest

object ArtworkProtoFixture {
    fun randomCreateArtworkRequestProto(
        style: ArtworkStyle = ArtworkStyle.ARTWORK_STYLE_POP_ART,
        artistId: String = ObjectId.get().toHexString(),
    ): CreateArtworkRequest = CreateArtworkRequest.newBuilder().also {
        it.title = getRandomString()
        it.description = getRandomString()
        it.artistId = artistId
        it.style = style
        it.width = Random.nextInt(10, 100)
        it.height = Random.nextInt(10, 100)
    }.build()
}
