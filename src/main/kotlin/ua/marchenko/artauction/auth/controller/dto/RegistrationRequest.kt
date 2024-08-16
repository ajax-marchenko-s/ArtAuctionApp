package ua.marchenko.artauction.auth.controller.dto

import ua.marchenko.artauction.user.enums.Role

data class RegistrationRequest(
    val name: String,
    val lastname: String,
    val email: String,
    val password: String,
    val role: Role
)
