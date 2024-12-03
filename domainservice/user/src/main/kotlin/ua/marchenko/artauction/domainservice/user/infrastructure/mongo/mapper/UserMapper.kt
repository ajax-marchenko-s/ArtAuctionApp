package ua.marchenko.artauction.domainservice.user.infrastructure.mongo.mapper

import ua.marchenko.artauction.domainservice.user.domain.CreateUser
import ua.marchenko.artauction.domainservice.user.domain.User
import ua.marchenko.artauction.domainservice.user.infrastructure.mongo.entity.MongoUser

fun MongoUser.toDomain(): User = User(
    requireNotNull(id) { "user id cannot be null" }.toHexString(),
    name ?: "unknown",
    lastName ?: "unknown",
    email ?: "unknown",
)

fun CreateUser.toMongo(): MongoUser = MongoUser(null, name, lastName, email)
