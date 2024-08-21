package ua.marchenko.artauction.user.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import ua.marchenko.artauction.user.controller.dto.UserResponse
import ua.marchenko.artauction.user.enums.Role
import kotlin.test.Test
import ua.marchenko.artauction.user.model.User
import user.random

class UserMapperTest {

    @Test
    fun `UserToUserResponse should return UserResponse if User has not null properties (except fields from bl)`() {
        //GIVEN
        val user = User.random(role = Role.ARTIST)
        val expectedUser =
            UserResponse(user.id!!.toString(), user.name!!, user.lastName!!, user.email!!, user.role!!)

        //WHEN
        val result = user.toUserResponse()

        //THEN
        assertEquals(expectedUser, result)
    }

    @Test
    fun `UserToUserResponse should set default values if User has null properties (except fields from bl)`() {
        //GIVEN
        val user = User.random(role = null)
        val expectedUser =
            UserResponse(user.id!!.toString(), user.name!!, user.lastName!!, user.email!!, Role.UNKNOWN)

        //WHEN
        val result = user.toUserResponse()

        //THEN
        assertEquals(expectedUser, result)
    }
}
