package ua.marchenko.artauction.auth.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.security.core.userdetails.UserDetails
import ua.marchenko.artauction.auth.data.CustomUserDetails
import auth.getRandomRegistrationRequest
import user.getRandomUser
import ua.marchenko.artauction.user.enums.Role
import ua.marchenko.artauction.user.model.User
import kotlin.test.Test

class AuthMapperTest {

    @Test
    fun `RegistrationRequestToUser should return User`() {
        //GIVEN
        val user = getRandomRegistrationRequest()
        val expectedUser = User(null, user.name, user.lastname, user.email, user.password, user.role)

        //WHEN
        val result = user.toUser()

        //THEN
        assertEquals(expectedUser, result)
    }

    @Test
    fun `UserToUserDetails should return UserDetails if User has right properties`() {
        //GIVEN
        val user = getRandomUser(role = Role.ARTIST)
        val expectedUserDetails: UserDetails =
            CustomUserDetails(user.email!!, user.password!!, user.role!!)

        //WHEN
        val result = user.toUserDetails()

        //THEN
        assertEquals(expectedUserDetails, result)
    }

    @Test
    fun `UserToUserDetails should set default values if User has null properties (except fields from bl)`() {
        //GIVEN
        val user = getRandomUser(role = null)
        val expectedUserDetails: UserDetails =
            CustomUserDetails(user.email!!, user.password!!, Role.UNKNOWN)

        //WHEN
        val result = user.toUserDetails()

        //THEN
        assertEquals(expectedUserDetails, result)
    }
}
