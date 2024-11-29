package ua.marchenko.artauction.domainservice.artwork.infrastructure.mongo.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import ua.marchenko.artauction.domainservice.artwork.domain.Artwork.ArtworkStatus
import ua.marchenko.artauction.domainservice.artwork.domain.Artwork.ArtworkStyle
import ua.marchenko.artauction.domainservice.artwork.infrastructure.mongo.entity.MongoArtwork.ArtworkStyle as ArtworkStyleMongo
import ua.marchenko.artauction.domainservice.artwork.infrastructure.mongo.entity.MongoArtwork.ArtworkStatus as ArtworkStatusMongo

class ArtworkMapperTest {
    @ParameterizedTest
    @MethodSource("artworkStyleDomainToArtworkStyleMongoData")
    fun `should map ArtworkStyle to ArtworkStyleMongo enum values`(
        valueFrom: ArtworkStyle,
        valueTo: ArtworkStyleMongo,
    ) {
        // WHEN THEN
        assertEquals(valueTo, valueFrom.toMongoStyle())
    }

    @ParameterizedTest
    @MethodSource("artworkStyleMongoToArtworkStyleDomainData")
    fun `should map ArtworkStyleMongo to ArtworkStyleDomain enum values`(
        valueFrom: ArtworkStyleMongo,
        valueTo: ArtworkStyle,
    ) {
        // WHEN THEN
        assertEquals(valueTo, valueFrom.toDomainStyle())
    }

    @ParameterizedTest
    @MethodSource("artworkStatusDomainToArtworkStatusMongoData")
    fun `should map ArtworkStatus to ArtworkStatusMongo enum values`(
        valueFrom: ArtworkStatus,
        valueTo: ArtworkStatusMongo,
    ) {
        // WHEN THEN
        assertEquals(valueTo, valueFrom.toMongoStatus())
    }

    @ParameterizedTest
    @MethodSource("artworkStatusMongoToArtworkStatusDomainData")
    fun `should map ArtworkStatusMongo to ArtworkStatus enum values`(
        valueFrom: ArtworkStatusMongo,
        valueTo: ArtworkStatus,
    ) {
        // WHEN THEN
        assertEquals(valueTo, valueFrom.toDomainStatus())
    }

    companion object {
        @JvmStatic
        fun artworkStyleDomainToArtworkStyleMongoData(): List<Arguments> = listOf(
            Arguments.of(ArtworkStyle.UNKNOWN, ArtworkStyleMongo.UNKNOWN),
            Arguments.of(ArtworkStyle.REALISM, ArtworkStyleMongo.REALISM),
            Arguments.of(ArtworkStyle.IMPRESSIONISM, ArtworkStyleMongo.IMPRESSIONISM),
            Arguments.of(ArtworkStyle.EXPRESSIONISM, ArtworkStyleMongo.EXPRESSIONISM),
            Arguments.of(ArtworkStyle.CUBISM, ArtworkStyleMongo.CUBISM),
            Arguments.of(ArtworkStyle.SURREALISM, ArtworkStyleMongo.SURREALISM),
            Arguments.of(ArtworkStyle.ABSTRACT, ArtworkStyleMongo.ABSTRACT),
            Arguments.of(ArtworkStyle.POP_ART, ArtworkStyleMongo.POP_ART),
            Arguments.of(ArtworkStyle.MINIMALISM, ArtworkStyleMongo.MINIMALISM),
            Arguments.of(ArtworkStyle.RENAISSANCE, ArtworkStyleMongo.RENAISSANCE)
        )

        @JvmStatic
        fun artworkStyleMongoToArtworkStyleDomainData(): List<Arguments> = listOf(
            Arguments.of(ArtworkStyleMongo.UNKNOWN, ArtworkStyle.UNKNOWN),
            Arguments.of(ArtworkStyleMongo.REALISM, ArtworkStyle.REALISM),
            Arguments.of(ArtworkStyleMongo.IMPRESSIONISM, ArtworkStyle.IMPRESSIONISM),
            Arguments.of(ArtworkStyleMongo.EXPRESSIONISM, ArtworkStyle.EXPRESSIONISM),
            Arguments.of(ArtworkStyleMongo.CUBISM, ArtworkStyle.CUBISM),
            Arguments.of(ArtworkStyleMongo.SURREALISM, ArtworkStyle.SURREALISM),
            Arguments.of(ArtworkStyleMongo.ABSTRACT, ArtworkStyle.ABSTRACT),
            Arguments.of(ArtworkStyleMongo.POP_ART, ArtworkStyle.POP_ART),
            Arguments.of(ArtworkStyleMongo.MINIMALISM, ArtworkStyle.MINIMALISM),
            Arguments.of(ArtworkStyleMongo.RENAISSANCE, ArtworkStyle.RENAISSANCE)
        )

        @JvmStatic
        fun artworkStatusDomainToArtworkStatusMongoData(): List<Arguments> = listOf(
            Arguments.of(ArtworkStatus.ON_AUCTION, ArtworkStatusMongo.ON_AUCTION),
            Arguments.of(ArtworkStatus.SOLD, ArtworkStatusMongo.SOLD),
            Arguments.of(ArtworkStatus.VIEW, ArtworkStatusMongo.VIEW),
            Arguments.of(ArtworkStatus.UNKNOWN, ArtworkStatusMongo.UNKNOWN),
        )

        @JvmStatic
        fun artworkStatusMongoToArtworkStatusDomainData(): List<Arguments> = listOf(
            Arguments.of(ArtworkStatusMongo.ON_AUCTION, ArtworkStatus.ON_AUCTION),
            Arguments.of(ArtworkStatusMongo.SOLD, ArtworkStatus.SOLD),
            Arguments.of(ArtworkStatusMongo.VIEW, ArtworkStatus.VIEW),
            Arguments.of(ArtworkStatusMongo.UNKNOWN, ArtworkStatus.UNKNOWN),
        )
    }
}
