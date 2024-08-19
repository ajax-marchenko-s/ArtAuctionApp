package ua.marchenko.artauction.auth.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.security.core.userdetails.UserDetails
import ua.marchenko.artauction.auth.data.CustomUserDetails
import ua.marchenko.artauction.common.auth.getRandomRegistrationRequest
import ua.marchenko.artauction.common.user.getRandomUser
import ua.marchenko.artauction.user.enums.Role
import ua.marchenko.artauction.user.model.User
import kotlin.test.Test

class AuthMapperTest {

    @Test
    fun `RegistrationRequestToUser should return User`() {
        val user = getRandomRegistrationRequest()
        val expectedUser = User(null, user.name, user.lastname, user.email, user.password, user.role)
        val result = user.toUser()
        assertEquals(expectedUser, result)
    }

    @Test
    fun `UserToUserDetails should return UserDetails if User has right properties`() {
        val user = getRandomUser(role = Role.ARTIST)
        val expectedUserDetails: UserDetails =
            CustomUserDetails(user.email ?: "", user.password ?: "", user.role ?: Role.ARTIST)
        val result = user.toUserDetails()
        assertEquals(expectedUserDetails, result)
    }
}
