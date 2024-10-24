package ua.marchenko.core.user.dto

import ua.marchenko.core.user.enums.Role

data class UserResponse(
    val id: String,
    val name: String,
    val lastName: String,
    val email: String,
    val role: Role,
)
