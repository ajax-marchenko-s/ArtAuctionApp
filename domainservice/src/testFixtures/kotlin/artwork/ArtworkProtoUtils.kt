package artwork

import getRandomString
import org.bson.types.ObjectId
import ua.marchenko.internal.commonmodels.artwork.ArtworkStyle as ArtworkStyleProto
import ua.marchenko.internal.input.reqreply.artwork.CreateArtworkRequest as CreateArtworkRequestProto

object ArtworkProtoFixture {

    fun randomCreateArtworkRequestProto(
        style: ArtworkStyleProto = ArtworkStyleProto.ARTWORK_STYLE_POP_ART,
        artistId: String = ObjectId.get().toHexString(),
    ): CreateArtworkRequestProto = CreateArtworkRequestProto.newBuilder()
        .setTitle(getRandomString())
        .setDescription(getRandomString())
        .setStyle(style)
        .setWidth(100)
        .setHeight(150)
        .setArtistId(artistId)
        .build()
}
