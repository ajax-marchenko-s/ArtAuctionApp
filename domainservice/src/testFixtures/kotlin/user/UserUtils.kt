package user

import getRandomString
import org.bson.types.ObjectId
import ua.marchenko.artauction.common.mongodb.id.toObjectId
import ua.marchenko.artauction.user.controller.dto.CreateUserRequest
import ua.marchenko.artauction.user.model.MongoUser
import ua.marchenko.core.user.enums.Role

fun MongoUser.Companion.random(
    id: String? = ObjectId().toHexString(),
    name: String? = getRandomString(10),
    lastName: String? = getRandomString(10),
    email: String? = getRandomString(10),
    password: String? = getRandomString(10),
    role: Role? = Role.ARTIST,
) = MongoUser(
    id = id?.toObjectId(),
    name = name,
    lastName = lastName,
    email = email,
    password = password,
    role = role,
)

fun CreateUserRequest.Companion.random() =
    CreateUserRequest(
        name = getRandomString(),
        lastname = getRandomString(),
        email = getRandomString(),
        password = getRandomString(),
        role = Role.ARTIST,
    )
