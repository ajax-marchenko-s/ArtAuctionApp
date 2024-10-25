package artwork

import getRandomString
import ua.marchenko.core.artwork.enums.ArtworkStyle
import ua.marchenko.gateway.artwork.controller.dto.CreateArtworkRequest

fun CreateArtworkRequest.Companion.random(style: ArtworkStyle = ArtworkStyle.POP_ART) =
    CreateArtworkRequest(
        title = getRandomString(),
        description = getRandomString(),
        style = style,
        width = 100,
        height = 150,
        artistId = getRandomString()
    )
