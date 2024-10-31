package ua.marchenko.artauction.user.mapper

import ua.marchenko.artauction.user.controller.dto.CreateUserRequest
import ua.marchenko.artauction.user.model.MongoUser
import ua.marchenko.core.user.dto.UserResponse

fun MongoUser.toResponse() = UserResponse(
    requireNotNull(id) { "user id cannot be null" }.toHexString(),
    name ?: "unknown",
    lastName ?: "unknown",
    email ?: "unknown",
)

fun CreateUserRequest.toMongo() = MongoUser(null, name, lastname, email)
