package ua.marchenko.artauction.auth.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.security.core.userdetails.UserDetails
import ua.marchenko.artauction.auth.data.CustomUserDetails
import auth.random
import ua.marchenko.artauction.user.enums.Role
import ua.marchenko.artauction.user.model.User
import kotlin.test.Test
import ua.marchenko.artauction.auth.controller.dto.RegistrationRequest
import user.random

class AuthMapperTest {

    @Test
    fun `should return User`() {
        //GIVEN
        val user = RegistrationRequest.random()
        val expectedUser = User(null, user.name, user.lastname, user.email, user.password, user.role)

        //WHEN
        val result = user.toUser()

        //THEN
        assertEquals(expectedUser, result)
    }

    @Test
    fun `should return UserDetails when User has right properties`() {
        //GIVEN
        val user = User.random(role = Role.ARTIST)
        val expectedUserDetails: UserDetails =
            CustomUserDetails(user.email!!, user.password!!, user.role!!)

        //WHEN
        val result = user.toUserDetails()

        //THEN
        assertEquals(expectedUserDetails, result)
    }

    @Test
    fun `should set default values when User has null properties (except fields from bl)`() {
        //GIVEN
        val user = User.random(role = null)
        val expectedUserDetails: UserDetails =
            CustomUserDetails(user.email!!, user.password!!, Role.UNKNOWN)

        //WHEN
        val result = user.toUserDetails()

        //THEN
        assertEquals(expectedUserDetails, result)
    }
}
