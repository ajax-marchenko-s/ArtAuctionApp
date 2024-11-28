package ua.marchenko.artauction.domainservice.user.infrastructure.mongo.mapper

import org.bson.types.ObjectId
import ua.marchenko.artauction.domainservice.user.domain.User
import ua.marchenko.artauction.domainservice.user.infrastructure.mongo.entity.MongoUser

fun MongoUser.toDomain(): User = User(
    requireNotNull(id) { "user id cannot be null" }.toHexString(),
    name ?: "unknown",
    lastName ?: "unknown",
    email ?: "unknown",
)

fun User.toMongo(): MongoUser = MongoUser(id?.let { ObjectId(it) }, name, lastName, email)
