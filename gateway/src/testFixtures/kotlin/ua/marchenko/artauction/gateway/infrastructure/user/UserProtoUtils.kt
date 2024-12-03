package ua.marchenko.artauction.gateway.infrastructure.user

import ua.marchenko.artauction.gateway.getRandomString
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
