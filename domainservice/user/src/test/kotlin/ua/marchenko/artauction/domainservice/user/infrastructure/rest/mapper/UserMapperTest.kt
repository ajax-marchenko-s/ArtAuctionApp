package ua.marchenko.artauction.domainservice.user.infrastructure.rest.mapper

import kotlin.test.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import ua.marchenko.artauction.domainservice.user.domain.User
import ua.marchenko.artauction.domainservice.user.domain.random
import ua.marchenko.artauction.domainservice.user.infrastructure.random
import ua.marchenko.artauction.domainservice.user.infrastructure.rest.dto.CreateUserRequest
import ua.marchenko.artauction.core.user.dto.UserResponse

class UserMapperTest {

    @Test
    fun `should return UserResponse from User domain`() {
        // GIVEN
        val user = User.random()
        val expectedUser =
            UserResponse(user.id!!, user.name, user.lastName, user.email)

        // WHEN
        val result = user.toResponse()

        // THEN
        assertEquals(expectedUser, result)
    }

    @Test
    fun `should throw exception when User domain id is null`() {
        // GIVEN
        val user = User.random(id = null)

        // WHEN THEN
        val exception = assertThrows<IllegalArgumentException> {
            user.toResponse()
        }
        assertEquals("user id cannot be null", exception.message)
    }

    @Test
    fun `should return User domain from CreateUserRequest`() {
        // GIVEN
        val request = CreateUserRequest.random()
        val expectedUser = User(null, request.name, request.lastname, request.email)

        // WHEN
        val result = request.toDomain()

        // THEN
        assertEquals(expectedUser, result)
    }
}
