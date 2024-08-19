package ua.marchenko.artauction.user.mapper

import ua.marchenko.artauction.user.controller.dto.UserResponse
import ua.marchenko.artauction.user.model.User

fun User.toUserResponse() = UserResponse(
    id ?: throwIllegalArgumentException("id"),
    name ?: throwIllegalArgumentException("name"),
    lastName ?: throwIllegalArgumentException("lastname"),
    email ?: throwIllegalArgumentException("email"),
    role ?: throwIllegalArgumentException("role")
)

private fun throwIllegalArgumentException(field: String): Nothing =
    throw IllegalArgumentException("User entity is in an invalid state: missing required field: $field")
