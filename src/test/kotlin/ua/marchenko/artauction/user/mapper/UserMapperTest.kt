package ua.marchenko.artauction.user.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import ua.marchenko.artauction.common.user.getRandomUser
import ua.marchenko.artauction.user.controller.dto.UserResponse
import ua.marchenko.artauction.user.enums.Role
import kotlin.test.Test

class UserMapperTest {

    @Test
    fun `UserToUserResponse should return UserResponse if User has not null properties (except fields from bl)`() {
        val user = getRandomUser(role = Role.ARTIST)
        val expectedUser =
            UserResponse(user.id ?: "", user.name ?: "", user.lastName ?: "", user.email ?: "", Role.ARTIST)
        val result = user.toUserResponse()
        assertEquals(expectedUser, result)
    }

    @Test
    fun `UserToUserResponse should throwIllegalArgumentException if User has null properties (except fields from bl)`() {
        val user = getRandomUser(role = Role.ARTIST, email = null)
        assertThrows<IllegalArgumentException> { user.toUserResponse() }
    }

}
