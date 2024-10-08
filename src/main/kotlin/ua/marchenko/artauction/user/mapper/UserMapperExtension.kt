package ua.marchenko.artauction.user.mapper

import ua.marchenko.artauction.user.controller.dto.UserResponse
import ua.marchenko.artauction.user.enums.Role
import ua.marchenko.artauction.user.model.MongoUser

fun MongoUser.toResponse() = UserResponse(
    requireNotNull(id) { "user id cannot be null" }.toHexString(),
    name ?: "unknown",
    lastName ?: "unknown",
    email ?: "unknown",
    role ?: Role.UNKNOWN,
)
