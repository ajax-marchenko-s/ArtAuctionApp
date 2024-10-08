package ua.marchenko.artauction.auth.service

import getRandomEmail
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.springframework.security.core.userdetails.UsernameNotFoundException
import ua.marchenko.artauction.auth.mapper.toUserDetails
import ua.marchenko.artauction.user.repository.UserRepository
import kotlin.test.Test
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test
import ua.marchenko.artauction.user.model.MongoUser
import user.random

class CustomUserDetailsServiceTest {

    @MockK
    private lateinit var mockUserRepository: UserRepository

    @InjectMockKs
    private lateinit var userDetailsService: CustomUserDetailsServiceImpl

    @Test
    fun `should return UserDetails by username when user with this email exists`() {
        // GIVEN
        val email = getRandomEmail()
        val user = MongoUser.random(email = email)

        every { mockUserRepository.findByEmail(email) } returns user.toMono()

        // WHEN
        val result = userDetailsService.findByUsername(email)

        // THEN
        result.test()
            .expectNext(user.toUserDetails())
            .verifyComplete()
    }

    @Test
    fun `should throw UsernameNotFoundException when there is no user with this email`() {
        // GIVEN
        val email = getRandomEmail()
        every { mockUserRepository.findByEmail(email) } returns Mono.empty()

        // WHEN
        val result = userDetailsService.findByUsername(email)

        // THEN
        result.test()
            .verifyError(UsernameNotFoundException::class.java)
    }
}
