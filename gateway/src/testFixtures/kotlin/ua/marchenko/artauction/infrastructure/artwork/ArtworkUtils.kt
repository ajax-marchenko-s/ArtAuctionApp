package ua.marchenko.artauction.infrastructure.artwork

import ua.marchenko.artauction.getRandomString
import kotlin.random.Random
import ua.marchenko.artauction.gateway.infrastructure.rest.dto.CreateArtworkRequest
import ua.marchenko.artauction.gateway.infrastructure.rest.dto.enums.ArtworkStyle

fun CreateArtworkRequest.Companion.random() =
    CreateArtworkRequest(
        title = getRandomString(),
        description = getRandomString(),
        style = ArtworkStyle.POP_ART,
        width = Random.nextInt(10, 100),
        height = Random.nextInt(10, 100),
        artistId = getRandomString()
    )
