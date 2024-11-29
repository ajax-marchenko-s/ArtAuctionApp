package ua.marchenko.artauction.domainservice.artwork.domain

import kotlin.random.Random
import ua.marchenko.artauction.domainservice.artwork.getRandomString
import org.bson.types.ObjectId
import ua.marchenko.artauction.domainservice.user.domain.User
import ua.marchenko.artauction.domainservice.artwork.domain.Artwork.ArtworkStatus
import ua.marchenko.artauction.domainservice.artwork.domain.projection.ArtworkFull
import ua.marchenko.artauction.domainservice.user.domain.random

fun Artwork.Companion.random(
    id: String? = ObjectId().toHexString(),
    artistId: String = ObjectId().toHexString(),
    status: ArtworkStatus = ArtworkStatus.VIEW,
): Artwork = Artwork(
    id = id,
    title = getRandomString(),
    description = getRandomString(),
    style = Artwork.ArtworkStyle.POP_ART,
    width = Random.nextInt(10, 100),
    height = Random.nextInt(10, 100),
    status = status,
    artistId = artistId,
)

fun CreateArtwork.Companion.random(
    artistId: String = ObjectId().toHexString(),
): CreateArtwork = CreateArtwork(
    title = getRandomString(),
    description = getRandomString(),
    style = Artwork.ArtworkStyle.POP_ART,
    width = Random.nextInt(10, 100),
    height = Random.nextInt(10, 100),
    artistId = artistId,
)

fun ArtworkFull.Companion.random(
    id: String = ObjectId().toHexString(),
    status: ArtworkStatus = ArtworkStatus.VIEW,
    title: String = getRandomString(),
) = ArtworkFull(
    id = id,
    title = title,
    description = getRandomString(),
    style = Artwork.ArtworkStyle.POP_ART,
    width = Random.nextInt(10, 100),
    height = Random.nextInt(10, 100),
    status = status,
    artist = User.random()
)
