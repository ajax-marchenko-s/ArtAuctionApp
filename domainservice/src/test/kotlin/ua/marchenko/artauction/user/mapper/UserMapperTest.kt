package ua.marchenko.artauction.user.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test
import org.junit.jupiter.api.assertThrows
import ua.marchenko.artauction.user.controller.dto.CreateUserRequest
import ua.marchenko.artauction.user.model.MongoUser
import ua.marchenko.core.user.dto.UserResponse
import ua.marchenko.core.user.enums.Role
import user.random

class UserMapperTest {

    @Test
    fun `should return UserResponse when User has not null properties (except fields from bl)`() {
        //GIVEN
        val user = MongoUser.random(role = Role.ARTIST)
        val expectedUser =
            UserResponse(user.id!!.toHexString(), user.name!!, user.lastName!!, user.email!!, user.role!!)

        //WHEN
        val result = user.toResponse()

        //THEN
        assertEquals(expectedUser, result)
    }

    @Test
    fun `should set default values when User has null properties (except fields from bl)`() {
        //GIVEN
        val user = MongoUser.random(role = null)
        val expectedUser =
            UserResponse(user.id!!.toHexString(), user.name!!, user.lastName!!, user.email!!, Role.UNKNOWN)

        //WHEN
        val result = user.toResponse()

        //THEN
        assertEquals(expectedUser, result)
    }

    @Test
    fun `should throw exception when User id is null`() {
        // GIVEN
        val user = MongoUser.random(id = null)

        // WHEN THEN
        val exception = assertThrows<IllegalArgumentException> {
            user.toResponse()
        }
        assertEquals("user id cannot be null", exception.message)
    }

    @Test
    fun `should return MongoUser from CreateUserRequest`() {
        //GIVEN
        val request = CreateUserRequest.random()
        val expectedUser =
            MongoUser(null, request.name, request.lastname, request.email, request.password, request.role)

        //WHEN
        val result = request.toMongo()

        //THEN
        assertEquals(expectedUser, result)
    }
}
