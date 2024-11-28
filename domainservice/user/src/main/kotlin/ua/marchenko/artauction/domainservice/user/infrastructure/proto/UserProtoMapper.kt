package ua.marchenko.artauction.domainservice.user.infrastructure.proto

import ua.marchenko.artauction.domainservice.user.domain.User
import ua.marchenko.commonmodels.user.User as UserProto

fun User.toUserProto(): UserProto {
    return UserProto.newBuilder().also {
        it.id = requireNotNull(id) { "user id cannot be null" }
        it.name = name
        it.lastName = lastName
        it.email = email
    }.build()
}
