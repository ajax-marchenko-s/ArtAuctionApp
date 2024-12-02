package ua.marchenko.artauction.domainservice.artwork.infrastructure.mongo.mapper

import ua.marchenko.artauction.domainservice.user.domain.User
import ua.marchenko.artauction.domainservice.user.infrastructure.mongo.mapper.toDomain
import ua.marchenko.artauction.domainservice.artwork.domain.Artwork
import ua.marchenko.artauction.domainservice.artwork.domain.CreateArtwork
import ua.marchenko.artauction.domainservice.artwork.domain.projection.ArtworkFull
import ua.marchenko.artauction.domainservice.artwork.infrastructure.mongo.entity.MongoArtwork
import ua.marchenko.artauction.domainservice.common.infrastructure.mongodb.id.toObjectId
import ua.marchenko.artauction.domainservice.artwork.infrastructure.mongo.entity.projection.MongoArtworkFull as MongoArtworkFull

fun MongoArtwork.toDomain() = Artwork(
    id = requireNotNull(id) { "artwork id cannot be null" }.toHexString(),
    title = title ?: "unknown",
    description = description ?: "unknown",
    style = requireNotNull(style) { "artwork style cannot be null" }.toDomainStyle(),
    width = width ?: 0,
    height = height ?: 0,
    status = requireNotNull(status) { "artwork status cannot be null" }.toDomainStatus(),
    artistId = artistId?.toHexString() ?: "unknown",
)

fun CreateArtwork.toMongo() = MongoArtwork(
    id = null,
    title = title,
    description = description,
    style = style.toMongoStyle(),
    width = width,
    height = height,
    status = status.toMongoStatus(),
    artistId = artistId.toObjectId(),
)

fun Artwork.toMongo() = MongoArtwork(
    id = id.toObjectId(),
    title = title,
    description = description,
    style = style.toMongoStyle(),
    width = width,
    height = height,
    status = status.toMongoStatus(),
    artistId = artistId.toObjectId(),
)

fun MongoArtworkFull.toDomain() = ArtworkFull(
    id = requireNotNull(id) { "artwork id cannot be null" }.toHexString(),
    title = title ?: "unknown",
    description = description ?: "unknown",
    style = requireNotNull(style) { "artwork style cannot be null" }.toDomainStyle(),
    width = width ?: 0,
    height = height ?: 0,
    status = requireNotNull(status) { "artwork status cannot be null" }.toDomainStatus(),
    artist = artist?.toDomain() ?: User.defaultDomain(),
)

fun MongoArtwork.ArtworkStatus.toDomainStatus(): Artwork.ArtworkStatus {
    return when (this) {
        MongoArtwork.ArtworkStatus.SOLD -> Artwork.ArtworkStatus.SOLD
        MongoArtwork.ArtworkStatus.ON_AUCTION -> Artwork.ArtworkStatus.ON_AUCTION
        MongoArtwork.ArtworkStatus.VIEW -> Artwork.ArtworkStatus.VIEW
        MongoArtwork.ArtworkStatus.UNKNOWN -> Artwork.ArtworkStatus.UNKNOWN
    }
}

fun Artwork.ArtworkStatus.toMongoStatus(): MongoArtwork.ArtworkStatus {
    return when (this) {
        Artwork.ArtworkStatus.SOLD -> MongoArtwork.ArtworkStatus.SOLD
        Artwork.ArtworkStatus.ON_AUCTION -> MongoArtwork.ArtworkStatus.ON_AUCTION
        Artwork.ArtworkStatus.VIEW -> MongoArtwork.ArtworkStatus.VIEW
        Artwork.ArtworkStatus.UNKNOWN -> MongoArtwork.ArtworkStatus.UNKNOWN
    }
}

fun MongoArtwork.ArtworkStyle.toDomainStyle(): Artwork.ArtworkStyle {
    return when (this) {
        MongoArtwork.ArtworkStyle.REALISM -> Artwork.ArtworkStyle.REALISM
        MongoArtwork.ArtworkStyle.IMPRESSIONISM -> Artwork.ArtworkStyle.IMPRESSIONISM
        MongoArtwork.ArtworkStyle.EXPRESSIONISM -> Artwork.ArtworkStyle.EXPRESSIONISM
        MongoArtwork.ArtworkStyle.CUBISM -> Artwork.ArtworkStyle.CUBISM
        MongoArtwork.ArtworkStyle.SURREALISM -> Artwork.ArtworkStyle.SURREALISM
        MongoArtwork.ArtworkStyle.ABSTRACT -> Artwork.ArtworkStyle.ABSTRACT
        MongoArtwork.ArtworkStyle.POP_ART -> Artwork.ArtworkStyle.POP_ART
        MongoArtwork.ArtworkStyle.MINIMALISM -> Artwork.ArtworkStyle.MINIMALISM
        MongoArtwork.ArtworkStyle.RENAISSANCE -> Artwork.ArtworkStyle.RENAISSANCE
        MongoArtwork.ArtworkStyle.UNKNOWN -> Artwork.ArtworkStyle.UNKNOWN
    }
}

fun Artwork.ArtworkStyle.toMongoStyle(): MongoArtwork.ArtworkStyle {
    return when (this) {
        Artwork.ArtworkStyle.REALISM -> MongoArtwork.ArtworkStyle.REALISM
        Artwork.ArtworkStyle.IMPRESSIONISM -> MongoArtwork.ArtworkStyle.IMPRESSIONISM
        Artwork.ArtworkStyle.EXPRESSIONISM -> MongoArtwork.ArtworkStyle.EXPRESSIONISM
        Artwork.ArtworkStyle.CUBISM -> MongoArtwork.ArtworkStyle.CUBISM
        Artwork.ArtworkStyle.SURREALISM -> MongoArtwork.ArtworkStyle.SURREALISM
        Artwork.ArtworkStyle.ABSTRACT -> MongoArtwork.ArtworkStyle.ABSTRACT
        Artwork.ArtworkStyle.POP_ART -> MongoArtwork.ArtworkStyle.POP_ART
        Artwork.ArtworkStyle.MINIMALISM -> MongoArtwork.ArtworkStyle.MINIMALISM
        Artwork.ArtworkStyle.RENAISSANCE -> MongoArtwork.ArtworkStyle.RENAISSANCE
        Artwork.ArtworkStyle.UNKNOWN -> MongoArtwork.ArtworkStyle.UNKNOWN
    }
}

private fun User.Companion.defaultDomain() = User(
    id = EMPTY_STRING,
    name = EMPTY_STRING,
    email = EMPTY_STRING,
    lastName = EMPTY_STRING,
)

private const val EMPTY_STRING = ""
