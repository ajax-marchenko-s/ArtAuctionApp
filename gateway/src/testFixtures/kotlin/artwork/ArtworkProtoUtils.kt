package artwork

import getRandomString
import user.UserProtoFixture
import ua.marchenko.internal.commonmodels.artwork.ArtworkStatus as ArtworkStatusProto
import ua.marchenko.internal.input.reqreply.artwork.CreateArtworkResponse as CreateArtworkResponseProto
import ua.marchenko.internal.commonmodels.artwork.Artwork as ArtworkProto
import ua.marchenko.internal.commonmodels.artwork.ArtworkFull as ArtworkFullProto
import ua.marchenko.internal.commonmodels.artwork.ArtworkStyle as ArtworkStyleProto
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkByIdResponse as FindArtworkByIdResponseProto
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkFullByIdResponse as FindArtworkFullByIdResponseProto
import ua.marchenko.internal.commonmodels.user.User as UserProto
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksResponse as FindAllArtworkResponseProto
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksFullResponse as FindAllArtworkFullResponseProto

object ArtworkProtoFixture {

    fun randomSuccessCreateArtworkResponseProto(
        style: ArtworkStyleProto = ArtworkStyleProto.ARTWORK_STYLE_POP_ART,
        status: ArtworkStatusProto = ArtworkStatusProto.ARTWORK_STATUS_ON_AUCTION,
    ): CreateArtworkResponseProto =
        CreateArtworkResponseProto.newBuilder()
            .setSuccess(
                CreateArtworkResponseProto.Success.newBuilder()
                    .setArtwork(randomArtworkProto(style = style, status = status))
                    .build()
            )
            .build()

    fun randomFailureGeneralCreateArtworkResponseProto(): CreateArtworkResponseProto =
        CreateArtworkResponseProto.newBuilder().also { builder ->
            builder.failureBuilder.message = ERROR_MESSAGE
        }
            .build()

    fun randomFailureUserNotFoundCreateArtworkResponseProto(): CreateArtworkResponseProto =
        CreateArtworkResponseProto.newBuilder().also { builder ->
            builder.failureBuilder.userNotFoundBuilder
            builder.failureBuilder.message = ERROR_MESSAGE
        }
            .build()

    fun randomSuccessFindArtworkByIdResponseProto(
        style: ArtworkStyleProto = ArtworkStyleProto.ARTWORK_STYLE_POP_ART,
        status: ArtworkStatusProto = ArtworkStatusProto.ARTWORK_STATUS_ON_AUCTION,
    ): FindArtworkByIdResponseProto =
        FindArtworkByIdResponseProto.newBuilder()
            .setSuccess(
                FindArtworkByIdResponseProto.Success.newBuilder()
                    .setArtwork(randomArtworkProto(style = style, status = status))
                    .build()
            )
            .build()

    fun randomFailureGeneralFindArtworkByIdResponseProto(): FindArtworkByIdResponseProto =
        FindArtworkByIdResponseProto.newBuilder()
            .setFailure(
                FindArtworkByIdResponseProto.Failure.newBuilder()
                    .setMessage(ERROR_MESSAGE)
                    .build()
            )
            .build()

    fun randomSuccessFindArtworkFullByIdResponseProto(
        style: ArtworkStyleProto = ArtworkStyleProto.ARTWORK_STYLE_POP_ART,
        status: ArtworkStatusProto = ArtworkStatusProto.ARTWORK_STATUS_ON_AUCTION,
        artist: UserProto = UserProtoFixture.randomUserProto()
    ): FindArtworkFullByIdResponseProto =
        FindArtworkFullByIdResponseProto.newBuilder().also { builder ->
            builder.successBuilder.setArtwork(randomArtworkFullProto(style = style, status = status, artist = artist))
        }
            .build()

    fun randomFailureGeneralFindArtworkFullByIdResponseProto(): FindArtworkFullByIdResponseProto =
        FindArtworkFullByIdResponseProto.newBuilder().also { builder ->
            builder.failureBuilder.setMessage(ERROR_MESSAGE)
        }
            .build()

    fun randomSuccessFindAllArtworkResponseProto(): FindAllArtworkResponseProto =
        FindAllArtworkResponseProto.newBuilder().also { builder ->
            builder.successBuilder.addAllArtworks(listOf(randomArtworkProto(), randomArtworkProto()))
        }
            .build()

    fun randomFailureGeneralFindAllArtworkResponseProto(): FindAllArtworkResponseProto =
        FindAllArtworkResponseProto.newBuilder()
            .setFailure(
                FindAllArtworkResponseProto.Failure.newBuilder()
                    .setMessage(ERROR_MESSAGE)
                    .build()
            )
            .build()

    fun randomSuccessFindAllArtworkFullResponseProto(): FindAllArtworkFullResponseProto =
        FindAllArtworkFullResponseProto.newBuilder().also { builder ->
            builder.successBuilder.addAllArtworks(listOf(randomArtworkFullProto(), randomArtworkFullProto()))
        }
            .build()

    fun randomFailureGeneralFindAllArtworkFullResponseProto(): FindAllArtworkFullResponseProto =
        FindAllArtworkFullResponseProto.newBuilder()
            .setFailure(
                FindAllArtworkFullResponseProto.Failure.newBuilder()
                    .setMessage(ERROR_MESSAGE)
                    .build()
            )
            .build()

    private fun randomArtworkProto(
        style: ArtworkStyleProto = ArtworkStyleProto.ARTWORK_STYLE_POP_ART,
        status: ArtworkStatusProto = ArtworkStatusProto.ARTWORK_STATUS_ON_AUCTION,
    ): ArtworkProto =
        ArtworkProto.newBuilder()
            .setId(getRandomString())
            .setTitle(getRandomString())
            .setDescription(getRandomString())
            .setStyle(style)
            .setStatus(status)
            .setWidth(100)
            .setHeight(150)
            .setArtistId(getRandomString())
            .build()

    private fun randomArtworkFullProto(
        style: ArtworkStyleProto = ArtworkStyleProto.ARTWORK_STYLE_POP_ART,
        status: ArtworkStatusProto = ArtworkStatusProto.ARTWORK_STATUS_ON_AUCTION,
        artist: UserProto = UserProtoFixture.randomUserProto()
    ): ArtworkFullProto =
        ArtworkFullProto.newBuilder()
            .setId(getRandomString())
            .setTitle(getRandomString())
            .setDescription(getRandomString())
            .setStyle(style)
            .setStatus(status)
            .setWidth(100)
            .setHeight(150)
            .setArtist(artist)
            .build()

    const val ERROR_MESSAGE = "Error message"
}
