package ua.marchenko.artauction.auth.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import ua.marchenko.artauction.auth.mapper.toUserDetails
import ua.marchenko.artauction.common.user.getRandomUser
import ua.marchenko.artauction.user.repository.UserRepository
import kotlin.test.Test

class CustomUserDetailsServiceTest {

    private val mockUserRepository = mock(UserRepository::class.java)

    private val userDetailsService: UserDetailsService = CustomUserDetailsServiceImpl(mockUserRepository)

    @Test
    fun `loadUserByUsername should return UserDetails by username if user with this email exists`() {
        val email = "email"
        val user = getRandomUser(email = email)
        `when`(mockUserRepository.findByEmail(email)).thenReturn(user)
        val result = userDetailsService.loadUserByUsername(email)
        assertEquals(result, user.toUserDetails())
    }

    @Test
    fun `loadUserByUsername should throw UsernameNotFoundException if there is no user with this email`() {
        val email = "email"
        `when`(mockUserRepository.findByEmail(email)).thenReturn(null)
        assertThrows<UsernameNotFoundException> { userDetailsService.loadUserByUsername(email) }
    }

}
