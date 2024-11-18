package artwork

import getRandomString
import kotlin.random.Random
import user.UserProtoFixture
import ua.marchenko.internal.commonmodels.artwork.Artwork.ArtworkStatus as ArtworkStatusProto
import ua.marchenko.internal.input.reqreply.artwork.CreateArtworkResponse as CreateArtworkResponseProto
import ua.marchenko.internal.commonmodels.artwork.Artwork as ArtworkProto
import ua.marchenko.internal.commonmodels.artwork.ArtworkFull as ArtworkFullProto
import ua.marchenko.internal.commonmodels.artwork.Artwork.ArtworkStyle as ArtworkStyleProto
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkByIdResponse as FindArtworkByIdResponseProto
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkFullByIdResponse as FindArtworkFullByIdResponseProto
import ua.marchenko.commonmodels.user.User as UserProto
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksResponse as FindAllArtworkResponseProto
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksFullResponse as FindAllArtworkFullResponseProto

object ArtworkProtoFixture {

    fun randomSuccessCreateArtworkResponseProto(
        style: ArtworkStyleProto = ArtworkStyleProto.ARTWORK_STYLE_POP_ART,
        status: ArtworkStatusProto = ArtworkStatusProto.ARTWORK_STATUS_ON_AUCTION,
    ): CreateArtworkResponseProto =
        CreateArtworkResponseProto.newBuilder().also { builder ->
            builder.successBuilder.setArtwork(randomArtworkProto(style = style, status = status))
        }.build()

    fun randomFailureGeneralCreateArtworkResponseProto(): CreateArtworkResponseProto =
        CreateArtworkResponseProto.newBuilder().also { builder ->
            builder.failureBuilder.message = ERROR_MESSAGE
        }.build()

    fun randomFailureUserNotFoundCreateArtworkResponseProto(): CreateArtworkResponseProto =
        CreateArtworkResponseProto.newBuilder().also { builder ->
            builder.failureBuilder.userNotFoundBuilder
            builder.failureBuilder.message = ERROR_MESSAGE
        }.build()

    fun randomSuccessFindArtworkByIdResponseProto(
        style: ArtworkStyleProto = ArtworkStyleProto.ARTWORK_STYLE_POP_ART,
        status: ArtworkStatusProto = ArtworkStatusProto.ARTWORK_STATUS_ON_AUCTION,
    ): FindArtworkByIdResponseProto =
        FindArtworkByIdResponseProto.newBuilder().also { builder ->
            builder.successBuilder.setArtwork(randomArtworkProto(style = style, status = status))
        }.build()

    fun randomFailureGeneralFindArtworkByIdResponseProto(): FindArtworkByIdResponseProto =
        FindArtworkByIdResponseProto.newBuilder().also { builder ->
            builder.failureBuilder.message = ERROR_MESSAGE
        }.build()

    fun randomSuccessFindArtworkFullByIdResponseProto(
        style: ArtworkStyleProto = ArtworkStyleProto.ARTWORK_STYLE_POP_ART,
        status: ArtworkStatusProto = ArtworkStatusProto.ARTWORK_STATUS_ON_AUCTION,
        artist: UserProto = UserProtoFixture.randomUserProto()
    ): FindArtworkFullByIdResponseProto =
        FindArtworkFullByIdResponseProto.newBuilder().also { builder ->
            builder.successBuilder.setArtwork(randomArtworkFullProto(style = style, status = status, artist = artist))
        }.build()

    fun randomFailureGeneralFindArtworkFullByIdResponseProto(): FindArtworkFullByIdResponseProto =
        FindArtworkFullByIdResponseProto.newBuilder().also { builder ->
            builder.failureBuilder.setMessage(ERROR_MESSAGE)
        }.build()

    fun randomSuccessFindAllArtworkResponseProto(): FindAllArtworkResponseProto =
        FindAllArtworkResponseProto.newBuilder().also { builder ->
            builder.successBuilder.addAllArtworks(listOf(randomArtworkProto(), randomArtworkProto()))
        }.build()

    fun randomFailureGeneralFindAllArtworkResponseProto(): FindAllArtworkResponseProto =
        FindAllArtworkResponseProto.newBuilder().also { builder ->
            builder.failureBuilder.message = ERROR_MESSAGE
        }.build()

    fun randomSuccessFindAllArtworkFullResponseProto(): FindAllArtworkFullResponseProto =
        FindAllArtworkFullResponseProto.newBuilder().also { builder ->
            builder.successBuilder.addAllArtworks(listOf(randomArtworkFullProto(), randomArtworkFullProto()))
        }.build()

    fun randomFailureGeneralFindAllArtworkFullResponseProto(): FindAllArtworkFullResponseProto =
        FindAllArtworkFullResponseProto.newBuilder().also { builder ->
            builder.failureBuilder.message = ERROR_MESSAGE
        }.build()

    private fun randomArtworkProto(
        style: ArtworkStyleProto = ArtworkStyleProto.ARTWORK_STYLE_POP_ART,
        status: ArtworkStatusProto = ArtworkStatusProto.ARTWORK_STATUS_ON_AUCTION,
    ): ArtworkProto =
        ArtworkProto.newBuilder().also {
            it.id = getRandomString()
            it.title = getRandomString()
            it.description = getRandomString()
            it.style = style
            it.status = status
            it.width = Random.nextInt(10, 100)
            it.height = Random.nextInt(10, 100)
            it.artistId = getRandomString()
        }.build()

    private fun randomArtworkFullProto(
        style: ArtworkStyleProto = ArtworkStyleProto.ARTWORK_STYLE_POP_ART,
        status: ArtworkStatusProto = ArtworkStatusProto.ARTWORK_STATUS_ON_AUCTION,
        artist: UserProto = UserProtoFixture.randomUserProto()
    ): ArtworkFullProto =
        ArtworkFullProto.newBuilder().also {
            it.id = getRandomString()
            it.title = getRandomString()
            it.description = getRandomString()
            it.style = style
            it.status = status
            it.width = Random.nextInt(10, 100)
            it.height = Random.nextInt(10, 100)
            it.artist = artist
        }.build()

    const val ERROR_MESSAGE = "Error message"
}
