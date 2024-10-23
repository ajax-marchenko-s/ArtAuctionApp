package ua.marchenko.core.artwork.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import ua.marchenko.core.artwork.enums.ArtworkStyle
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class CreateArtworkRequest @JsonCreator constructor(

    @JsonProperty("title")
    @field:NotBlank(message = "Artwork title cannot be blank")
    val title: String,

    @JsonProperty("description")
    @field:NotBlank(message = "Artwork description cannot be blank")
    val description: String,

    @JsonProperty("style")
    val style: ArtworkStyle,

    @JsonProperty("width")
    @field:Min(value = 1, message = "Artwork width must be greater than zero")
    val width: Int,

    @JsonProperty("height")
    @field:Min(value = 1, message = "Artwork height must be greater than zero")
    val height: Int,

    @JsonProperty("artistId")
    @field:NotBlank(message = "ArtistId cannot be blank")
    val artistId: String,
) {
    companion object
}
