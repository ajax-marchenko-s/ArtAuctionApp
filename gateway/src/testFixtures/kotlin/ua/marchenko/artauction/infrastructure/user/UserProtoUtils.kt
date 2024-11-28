package ua.marchenko.artauction.infrastructure.user

import ua.marchenko.artauction.getRandomString
import ua.marchenko.commonmodels.user.User as UserProto

object UserProtoFixture {
    fun randomUserProto(): UserProto =
        UserProto.newBuilder().apply {
            id = getRandomString()
            name = getRandomString()
            lastName = getRandomString()
            email = getRandomString()
        }.build()
}
