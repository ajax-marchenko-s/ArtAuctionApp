package ua.marchenko.artauction.domainservice.user.domain

import org.bson.types.ObjectId
import ua.marchenko.artauction.domainservice.user.getRandomString

fun User.Companion.random(
    id: String? = ObjectId().toHexString(),
    name: String = getRandomString(),
    lastName: String = getRandomString(),
    email: String = getRandomString(),
) = User(id = id, name = name, lastName = lastName, email = email)
