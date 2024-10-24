package ua.marchenko.artauction.user.mapper

import ua.marchenko.core.user.dto.UserResponse
import ua.marchenko.core.user.enums.Role
import ua.marchenko.internal.commonmodels.user.User as UserProto
import ua.marchenko.internal.commonmodels.user.UserRole as UserRoleProto

fun UserResponse.toUserProto(): UserProto {
    return UserProto.newBuilder()
        .setId(id)
        .setName(name)
        .setLastName(lastName)
        .setEmail(email)
        .setRole(role.toUserRoleProto())
        .build()
}

fun Role.toUserRoleProto(): UserRoleProto = runCatching { UserRoleProto.valueOf(this.name) }
    .getOrDefault(UserRoleProto.ROLE_UNSPECIFIED)
