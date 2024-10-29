package artwork

import getRandomInt
import getRandomString
import ua.marchenko.core.artwork.enums.ArtworkStyle
import ua.marchenko.gateway.artwork.controller.dto.CreateArtworkRequest

fun CreateArtworkRequest.Companion.random(style: ArtworkStyle = ArtworkStyle.POP_ART) =
    CreateArtworkRequest(
        title = getRandomString(),
        description = getRandomString(),
        style = style,
        width = getRandomInt(),
        height = getRandomInt(),
        artistId = getRandomString()
    )
