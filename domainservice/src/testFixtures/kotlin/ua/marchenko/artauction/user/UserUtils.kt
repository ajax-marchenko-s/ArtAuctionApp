package ua.marchenko.artauction.user

import ua.marchenko.artauction.getRandomString
import org.bson.types.ObjectId
import ua.marchenko.artauction.common.mongodb.id.toObjectId
import ua.marchenko.artauction.user.controller.dto.CreateUserRequest
import ua.marchenko.artauction.user.model.MongoUser

fun MongoUser.Companion.random(
    id: String? = ObjectId().toHexString(),
    name: String? = getRandomString(),
    lastName: String? = getRandomString(),
    email: String? = getRandomString(),
) = MongoUser(id = id?.toObjectId(), name = name, lastName = lastName, email = email)

fun CreateUserRequest.Companion.random() =
    CreateUserRequest(name = getRandomString(), lastname = getRandomString(), email = getRandomString())
