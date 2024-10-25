package ua.marchenko.artauction.user.mapper

import ua.marchenko.artauction.user.model.MongoUser
import ua.marchenko.core.user.enums.Role
import ua.marchenko.internal.commonmodels.user.User as UserProto
import ua.marchenko.internal.commonmodels.user.UserRole as UserRoleProto

fun MongoUser.toUserProto(): UserProto {
    return UserProto.newBuilder().also {
        it.id = requireNotNull(id) { "user id cannot be null" }.toHexString()
        it.name = name ?: "unknown"
        it.lastName = lastName ?: "unknown"
        it.email = email ?: "unknown"
        it.role = (role ?: Role.UNKNOWN).toUserRoleProto()
    }.build()
}

fun Role.toUserRoleProto(): UserRoleProto {
    return when (this) {
        Role.ARTIST -> UserRoleProto.ROLE_ARTIST
        Role.BUYER -> UserRoleProto.ROLE_BUYER
        Role.UNKNOWN -> UserRoleProto.ROLE_UNSPECIFIED
    }
}
