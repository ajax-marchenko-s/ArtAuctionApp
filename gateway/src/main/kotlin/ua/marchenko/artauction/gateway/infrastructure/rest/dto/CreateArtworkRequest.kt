package ua.marchenko.artauction.gateway.infrastructure.rest.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import ua.marchenko.artauction.gateway.infrastructure.rest.configuration.validation.StyleNotInSubset
import ua.marchenko.artauction.gateway.infrastructure.rest.dto.enums.ArtworkStyle

data class CreateArtworkRequest(

    @field:NotBlank(message = "Artwork title cannot be blank")
    val title: String,

    @field:NotBlank(message = "Artwork description cannot be blank")
    val description: String,

    @field:StyleNotInSubset(excluded = [ArtworkStyle.UNKNOWN])
    val style: ArtworkStyle,

    @field:Min(value = 1, message = "Artwork width must be greater than zero")
    val width: Int,

    @field:Min(value = 1, message = "Artwork height must be greater than zero")
    val height: Int,

    @field:NotBlank(message = "ArtistId cannot be blank")
    val artistId: String,
) {
    companion object
}
