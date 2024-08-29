package ua.marchenko.artauction.auth.controller.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import ua.marchenko.artauction.user.enums.Role

data class RegistrationRequest(
    @field:NotBlank(message = "Name cannot be blank")
    val name: String,

    @field:NotBlank(message = "Lastname cannot be blank")
    val lastname: String,

    @field:NotBlank(message = "Email cannot be blank")
    @field:Email(message = "Email should be valid")
    val email: String,

    @field:NotBlank(message = "Password cannot be blank")
    val password: String,

    @field:NotNull(message = "Role cannot be null")
    val role: Role,
) {
    companion object
}
