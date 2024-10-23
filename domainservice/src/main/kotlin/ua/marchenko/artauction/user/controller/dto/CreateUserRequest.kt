package ua.marchenko.artauction.user.controller.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import ua.marchenko.core.user.enums.Role

data class CreateUserRequest(
    @field:NotBlank(message = "Name cannot be blank")
    val name: String,

    @field:NotBlank(message = "Lastname cannot be blank")
    val lastname: String,

    @field:NotBlank(message = "Email cannot be blank")
    @field:Email(message = "Email should be valid")
    val email: String,

    @field:NotBlank(message = "Password cannot be blank")
    val password: String,

    val role: Role,
) {
    companion object
}
