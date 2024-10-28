package ua.marchenko.gateway.user

import ua.marchenko.internal.commonmodels.user.User as UserProto
import ua.marchenko.core.user.dto.UserResponse

fun UserProto.toUserResponse(): UserResponse =
    UserResponse(id = id, name = name, lastName = lastName, email = email)
