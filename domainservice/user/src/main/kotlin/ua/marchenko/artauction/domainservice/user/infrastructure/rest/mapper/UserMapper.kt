package ua.marchenko.artauction.domainservice.user.infrastructure.rest.mapper

import ua.marchenko.artauction.domainservice.user.domain.User
import ua.marchenko.artauction.domainservice.user.infrastructure.rest.dto.CreateUserRequest
import ua.marchenko.artauction.core.user.dto.UserResponse

fun User.toResponse() = UserResponse(
    id = requireNotNull(id) { "user id cannot be null" },
    name = name,
    lastName = lastName,
    email = email,
)

fun CreateUserRequest.toDomain() = User(null, name, lastname, email)
