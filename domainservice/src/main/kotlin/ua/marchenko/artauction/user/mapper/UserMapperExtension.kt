package ua.marchenko.artauction.user.mapper

import ua.marchenko.artauction.user.controller.dto.CreateUserRequest
import ua.marchenko.artauction.user.model.MongoUser
import ua.marchenko.core.user.dto.UserResponse
import ua.marchenko.core.user.enums.Role

fun MongoUser.toResponse() = UserResponse(
    requireNotNull(id) { "user id cannot be null" }.toHexString(),
    name ?: "unknown",
    lastName ?: "unknown",
    email ?: "unknown",
    role ?: Role.UNKNOWN,
)

fun CreateUserRequest.toMongo() = MongoUser(null, name, lastname, email, password, role)
