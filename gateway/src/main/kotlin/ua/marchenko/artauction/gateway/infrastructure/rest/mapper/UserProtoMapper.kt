package ua.marchenko.artauction.gateway.infrastructure.rest.mapper

import ua.marchenko.artauction.core.user.dto.UserResponse
import ua.marchenko.commonmodels.user.User as UserProto

fun UserProto.toUserResponse(): UserResponse =
    UserResponse(id = id, name = name, lastName = lastName, email = email)
