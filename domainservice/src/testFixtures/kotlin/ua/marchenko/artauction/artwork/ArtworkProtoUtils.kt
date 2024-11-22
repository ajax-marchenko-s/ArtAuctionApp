package ua.marchenko.artauction.artwork

import ua.marchenko.artauction.getRandomString
import kotlin.random.Random
import org.bson.types.ObjectId
import ua.marchenko.internal.commonmodels.artwork.Artwork.ArtworkStyle as ArtworkStyleProto
import ua.marchenko.internal.input.reqreply.artwork.CreateArtworkRequest as CreateArtworkRequestProto

object ArtworkProtoFixture {

    fun randomCreateArtworkRequestProto(
        style: ArtworkStyleProto = ArtworkStyleProto.ARTWORK_STYLE_POP_ART,
        artistId: String = ObjectId.get().toHexString(),
    ): CreateArtworkRequestProto = CreateArtworkRequestProto.newBuilder().also {
        it.title = getRandomString()
        it.description = getRandomString()
        it.artistId = artistId
        it.style = style
        it.width = Random.nextInt(10, 100)
        it.height = Random.nextInt(10, 100)
    }.build()
}