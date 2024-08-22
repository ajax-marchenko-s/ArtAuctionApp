package ua.marchenko.artauction.auth.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import ua.marchenko.artauction.auth.mapper.toUserDetails
import ua.marchenko.artauction.user.repository.UserRepository
import kotlin.test.Test
import getRandomEmail
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.whenever
import ua.marchenko.artauction.user.model.User
import user.random

class CustomUserDetailsServiceTest {

    private val mockUserRepository = mock(UserRepository::class.java)

    private val userDetailsService: UserDetailsService = CustomUserDetailsServiceImpl(mockUserRepository)

    @Test
    fun `loadUserByUsername should return UserDetails by username if user with this email exists`() {
        //GIVEN
        val email = getRandomEmail()
        val user = User.random(email = email)

        whenever(mockUserRepository.findByEmail(email)) doReturn (user)

        //WHEN
        val result = userDetailsService.loadUserByUsername(email)

        //THEN
        assertEquals(result, user.toUserDetails())
    }

    @Test
    fun `loadUserByUsername should throw UsernameNotFoundException if there is no user with this email`() {
        //GIVEN
        val email = getRandomEmail()
        whenever(mockUserRepository.findByEmail(email)) doReturn (null)

        //WHEN-THEN
        assertThrows<UsernameNotFoundException> { userDetailsService.loadUserByUsername(email) }
    }
}
