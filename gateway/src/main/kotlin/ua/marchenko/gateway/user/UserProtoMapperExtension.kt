package ua.marchenko.gateway.user

import ua.marchenko.internal.commonmodels.user.UserRole as UserRoleProto
import ua.marchenko.core.user.dto.UserResponse
import ua.marchenko.core.user.enums.Role
import ua.marchenko.internal.commonmodels.user.User as UserProto

fun UserProto.toUserResponse(): UserResponse =
    UserResponse(id, name = name, lastName = lastName, email = email, role = role.toRole())

fun UserRoleProto.toRole(): Role = runCatching { Role.valueOf(this.name) }
    .getOrDefault(Role.UNKNOWN)
