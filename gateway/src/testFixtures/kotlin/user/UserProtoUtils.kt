package user

import getRandomString
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
