package ua.marchenko.artauction.artwork.controller.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import ua.marchenko.artauction.artwork.enums.ArtworkStyle

data class CreateArtworkRequest(

    @field:NotBlank(message = "Artwork title cannot be blank")
    val title: String,

    @field:NotBlank(message = "Artwork description cannot be blank")
    val description: String,

    @field:NotNull(message = "Artwork style cannot be null")
    val style: ArtworkStyle,

    @field:Min(value = 1, message = "Artwork width must be greater than zero")
    val width: Int,

    @field:Min(value = 1, message = "Artwork height must be greater than zero")
    val height: Int,
) {
    companion object
}
