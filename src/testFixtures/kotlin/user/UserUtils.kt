package user

import getRandomObjectId
import getRandomString
import ua.marchenko.artauction.common.mongodb.id.toObjectId
import ua.marchenko.artauction.user.enums.Role
import ua.marchenko.artauction.user.model.User

fun User.Companion.random(
    id: String = getRandomObjectId().toString(),
    role: Role? = Role.ARTIST,
    email: String? = getRandomString(10),
) = User(
    id = id.toObjectId(),
    name = getRandomString(),
    lastName = getRandomString(),
    email = email,
    password = getRandomString(),
    role = role,
)
