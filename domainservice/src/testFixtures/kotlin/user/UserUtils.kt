package user

import getRandomString
import org.bson.types.ObjectId
import ua.marchenko.artauction.common.mongodb.id.toObjectId
import ua.marchenko.artauction.user.controller.dto.CreateUserRequest
import ua.marchenko.artauction.user.model.MongoUser

fun MongoUser.Companion.random(
    id: String? = ObjectId().toHexString(),
    name: String? = getRandomString(10),
    lastName: String? = getRandomString(10),
    email: String? = getRandomString(10),
) = MongoUser(id = id?.toObjectId(), name = name, lastName = lastName, email = email)

fun CreateUserRequest.Companion.random() =
    CreateUserRequest(name = getRandomString(), lastname = getRandomString(), email = getRandomString())
