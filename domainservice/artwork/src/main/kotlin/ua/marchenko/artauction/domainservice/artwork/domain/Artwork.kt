package ua.marchenko.artauction.domainservice.artwork.domain

data class Artwork(
    val id: String,
    val title: String,
    val description: String,
    val style: ArtworkStyle,
    val width: Int,
    val height: Int,
    val status: ArtworkStatus,
    val artistId: String,
) {

    enum class ArtworkStyle {
        REALISM,
        IMPRESSIONISM,
        EXPRESSIONISM,
        CUBISM,
        SURREALISM,
        ABSTRACT,
        POP_ART,
        MINIMALISM,
        RENAISSANCE,
        UNKNOWN,
    }

    enum class ArtworkStatus {
        SOLD,
        ON_AUCTION,
        VIEW,
        UNKNOWN,
    }

    companion object
}
