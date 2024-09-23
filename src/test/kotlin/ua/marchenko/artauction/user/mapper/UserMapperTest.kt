package ua.marchenko.artauction.user.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import ua.marchenko.artauction.user.controller.dto.UserResponse
import ua.marchenko.artauction.user.enums.Role
import kotlin.test.Test
import org.junit.jupiter.api.assertThrows
import ua.marchenko.artauction.user.model.MongoUser
import user.random

class UserMapperTest {

    @Test
    fun `should return UserResponse if User has not null properties (except fields from bl)`() {
        //GIVEN
        val user = MongoUser.random(role = Role.ARTIST)
        val expectedUser =
            UserResponse(user.id!!.toHexString(), user.name!!, user.lastName!!, user.email!!, user.role!!)

        //WHEN
        val result = user.toUserResponse()

        //THEN
        assertEquals(expectedUser, result)
    }

    @Test
    fun `should set default values if User has null properties (except fields from bl)`() {
        //GIVEN
        val user = MongoUser.random(role = null)
        val expectedUser =
            UserResponse(user.id!!.toHexString(), user.name!!, user.lastName!!, user.email!!, Role.UNKNOWN)

        //WHEN
        val result = user.toUserResponse()

        //THEN
        assertEquals(expectedUser, result)
    }

    @Test
    fun `should throw exception when User id is null`() {
        // GIVEN
        val user = MongoUser.random(id = null)

        // WHEN THEN
        val exception = assertThrows<IllegalArgumentException> {
            user.toUserResponse()
        }
        assertEquals("user id cannot be null", exception.message)
    }
}
