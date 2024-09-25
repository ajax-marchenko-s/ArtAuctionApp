package user

import getRandomString
import org.bson.types.ObjectId
import ua.marchenko.artauction.common.mongodb.id.toObjectId
import ua.marchenko.artauction.user.enums.Role
import ua.marchenko.artauction.user.model.MongoUser

fun MongoUser.Companion.random(
    id: String? = ObjectId().toHexString(),
    role: Role? = Role.ARTIST,
    email: String? = getRandomString(10),
) = MongoUser(
    id = id?.toObjectId(),
    name = getRandomString(),
    lastName = getRandomString(),
    email = email,
    password = getRandomString(),
    role = role,
)
