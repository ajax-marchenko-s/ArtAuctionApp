package ua.marchenko.artauction.dto.user

import ua.marchenko.artauction.enums.user.Role

data class UserRequest(
    val id: String,
    val username: String,
    val email: String,
    val password: String,
    val role: Role
)