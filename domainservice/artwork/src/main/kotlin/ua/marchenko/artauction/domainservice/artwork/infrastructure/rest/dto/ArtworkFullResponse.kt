package ua.marchenko.artauction.domainservice.artwork.infrastructure.rest.dto

import ua.marchenko.artauction.core.user.dto.UserResponse

data class ArtworkFullResponse(
    val id: String,
    val title: String,
    val description: String,
    val style: ArtworkStyle,
    val width: Int,
    val height: Int,
    val status: ArtworkStatus,
    val artist: UserResponse,
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
}
