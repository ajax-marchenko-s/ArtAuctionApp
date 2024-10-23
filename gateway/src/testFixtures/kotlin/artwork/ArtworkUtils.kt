package artwork

import getRandomString
import ua.marchenko.core.artwork.dto.CreateArtworkRequest
import ua.marchenko.core.artwork.enums.ArtworkStyle

fun CreateArtworkRequest.Companion.random(style: ArtworkStyle = ArtworkStyle.POP_ART) =
    CreateArtworkRequest(
        title = getRandomString(),
        description = getRandomString(),
        style = style,
        width = 100,
        height = 150,
        artistId = getRandomString()
    )
