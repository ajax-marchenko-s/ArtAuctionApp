package user

import getRandomString
import ua.marchenko.commonmodels.user.User as UserProto

object UserProtoFixture {
    fun randomUserProto(): UserProto =
        UserProto.newBuilder().also {
            it.id = getRandomString()
            it.name = getRandomString()
            it.lastName = getRandomString()
            it.email = getRandomString()
        }.build()
}
