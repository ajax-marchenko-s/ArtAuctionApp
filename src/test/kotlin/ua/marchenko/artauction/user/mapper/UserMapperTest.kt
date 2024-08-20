package ua.marchenko.artauction.user.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import user.getRandomUser
import ua.marchenko.artauction.user.controller.dto.UserResponse
import ua.marchenko.artauction.user.enums.Role
import kotlin.test.Test

class UserMapperTest {

    @Test
    fun `UserToUserResponse should return UserResponse if User has not null properties (except fields from bl)`() {
        //GIVEN
        val user = getRandomUser(role = Role.ARTIST)
        val expectedUser =
            UserResponse(user.id!!, user.name!!, user.lastName!!, user.email!!, user.role!!)

        //WHEN
        val result = user.toUserResponse()

        //THEN
        assertEquals(expectedUser, result)
    }

    @Test
    fun `UserToUserResponse should set default values if User has null properties (except fields from bl)`() {
        //GIVEN
        val user = getRandomUser(role = null)
        val expectedUser =
            UserResponse(user.id!!, user.name!!, user.lastName!!, user.email!!, Role.UNKNOWN)

        //WHEN
        val result = user.toUserResponse()

        //THEN
        assertEquals(expectedUser, result)
    }
}
