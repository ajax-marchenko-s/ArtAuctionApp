package ua.marchenko.gateway.user

import ua.marchenko.internal.commonmodels.user.UserRole as UserRoleProto
import ua.marchenko.internal.commonmodels.user.User as UserProto
import ua.marchenko.core.user.enums.Role
import ua.marchenko.core.user.dto.UserResponse

fun UserProto.toUserResponse(): UserResponse =
    UserResponse(id = id, name = name, lastName = lastName, email = email, role = role.toRole())

private fun UserRoleProto.toRole(): Role {
    return when (this) {
        UserRoleProto.ROLE_ARTIST -> Role.ARTIST
        UserRoleProto.ROLE_BUYER -> Role.BUYER
        UserRoleProto.ROLE_UNSPECIFIED -> Role.UNKNOWN
        UserRoleProto.UNRECOGNIZED -> Role.UNKNOWN
    }
}
