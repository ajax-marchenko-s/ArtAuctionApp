package ua.marchenko.artauction.artwork

import ua.marchenko.artauction.getRandomString
import kotlin.random.Random
import ua.marchenko.core.artwork.enums.ArtworkStyle
import ua.marchenko.gateway.artwork.controller.dto.CreateArtworkRequest

fun CreateArtworkRequest.Companion.random(style: ArtworkStyle = ArtworkStyle.POP_ART) =
    CreateArtworkRequest(
        title = getRandomString(),
        description = getRandomString(),
        style = style,
        width = Random.nextInt(10, 100),
        height = Random.nextInt(10, 100),
        artistId = getRandomString()
    )
