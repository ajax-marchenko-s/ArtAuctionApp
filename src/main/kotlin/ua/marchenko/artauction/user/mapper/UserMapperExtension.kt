package ua.marchenko.artauction.user.mapper

import ua.marchenko.artauction.user.controller.dto.UserResponse
import ua.marchenko.artauction.user.enums.Role
import ua.marchenko.artauction.user.model.User

fun User.toUserResponse() = UserResponse(
    id?.toString() ?: "unknown",
    name ?: "unknown",
    lastName ?: "unknown",
    email ?: "unknown",
    role ?: Role.UNKNOWN,
)
