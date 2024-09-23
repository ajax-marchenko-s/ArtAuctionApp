package ua.marchenko.artauction.auth.service

import getRandomEmail
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.springframework.security.core.userdetails.UsernameNotFoundException
import ua.marchenko.artauction.auth.mapper.toUserDetails
import ua.marchenko.artauction.user.repository.UserRepository
import kotlin.test.Test
import ua.marchenko.artauction.user.model.MongoUser
import user.random

class CustomUserDetailsServiceTest {

    @MockK
    private lateinit var mockUserRepository: UserRepository

    @InjectMockKs
    private lateinit var userDetailsService: CustomUserDetailsServiceImpl

    @Test
    fun `should return UserDetails by username when user with this email exists`() {
        //GIVEN
        val email = getRandomEmail()
        val user = MongoUser.random(email = email)

        every { mockUserRepository.findByEmail(email) } returns user

        //WHEN
        val result = userDetailsService.loadUserByUsername(email)

        //THEN
        assertEquals(result, user.toUserDetails())
    }

    @Test
    fun `should throw UsernameNotFoundException when there is no user with this email`() {
        //GIVEN
        val email = getRandomEmail()
        every { mockUserRepository.findByEmail(email) } returns null

        //WHEN //THEN
        assertThrows<UsernameNotFoundException> { userDetailsService.loadUserByUsername(email) }
    }
}
