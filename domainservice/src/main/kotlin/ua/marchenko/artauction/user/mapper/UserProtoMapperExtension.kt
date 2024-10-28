package ua.marchenko.artauction.user.mapper

import ua.marchenko.artauction.user.model.MongoUser
import ua.marchenko.internal.commonmodels.user.User as UserProto

fun MongoUser.toUserProto(): UserProto {
    return UserProto.newBuilder().also {
        it.id = requireNotNull(id) { "user id cannot be null" }.toHexString()
        it.name = name ?: "unknown"
        it.lastName = lastName ?: "unknown"
        it.email = email ?: "unknown"
    }.build()
}
