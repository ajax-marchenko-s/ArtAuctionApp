package ua.marchenko.artauction.domainservice.user.infrastructure.rest.mapper

import ua.marchenko.artauction.domainservice.user.domain.User
import ua.marchenko.artauction.domainservice.user.infrastructure.rest.dto.CreateUserRequest
import ua.marchenko.artauction.core.user.dto.UserResponse
import ua.marchenko.artauction.domainservice.user.domain.CreateUser

fun User.toResponse() = UserResponse(
    id = id,
    name = name,
    lastName = lastName,
    email = email,
)

fun CreateUserRequest.toDomainCreate() = CreateUser(name, lastname, email)
