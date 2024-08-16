package ua.marchenko.artauction.user.controller.dto

import ua.marchenko.artauction.user.enums.Role

data class UserResponse(
    val id: String,
    val name: String,
    val lastName: String,
    val email: String,
    val role: Role
)