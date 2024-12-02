package ua.marchenko.artauction.domainservice.user.infrastructure.rest.mapper

import kotlin.test.Test
import org.junit.jupiter.api.Assertions.assertEquals
import ua.marchenko.artauction.domainservice.user.domain.User
import ua.marchenko.artauction.domainservice.user.domain.random
import ua.marchenko.artauction.domainservice.user.infrastructure.random
import ua.marchenko.artauction.domainservice.user.infrastructure.rest.dto.CreateUserRequest
import ua.marchenko.artauction.core.user.dto.UserResponse
import ua.marchenko.artauction.domainservice.user.domain.CreateUser

class UserMapperTest {

    @Test
    fun `should return UserResponse from User domain`() {
        // GIVEN
        val user = User.random()
        val expectedUser =
            UserResponse(user.id, user.name, user.lastName, user.email)

        // WHEN
        val result = user.toResponse()

        // THEN
        assertEquals(expectedUser, result)
    }

    @Test
    fun `should return CreateUser domain from CreateUserRequest`() {
        // GIVEN
        val request = CreateUserRequest.random()
        val expectedUser = CreateUser(request.name, request.lastname, request.email)

        // WHEN
        val result = request.toDomainCreate()

        // THEN
        assertEquals(expectedUser, result)
    }
}
