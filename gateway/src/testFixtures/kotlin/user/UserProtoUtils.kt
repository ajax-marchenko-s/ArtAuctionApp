package user

import getRandomString
import ua.marchenko.internal.commonmodels.user.UserRole as UserRoleProto
import ua.marchenko.internal.commonmodels.user.User as UserProto

object UserProtoFixture {
    fun randomUserProto(): UserProto =
        UserProto.newBuilder()
            .setId(getRandomString())
            .setName(getRandomString())
            .setLastName(getRandomString())
            .setEmail(getRandomString())
            .setRole(UserRoleProto.ARTIST)
            .build()
}