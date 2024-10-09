package ua.marchenko.artauction.auth.filter

import getRandomEmail
import getRandomString
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import ua.marchenko.artauction.auth.service.CustomUserDetailsServiceImpl
import org.junit.jupiter.api.Assertions.assertNull
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import reactor.kotlin.test.test
import ua.marchenko.artauction.auth.jwt.JwtAuthenticationFilter
import ua.marchenko.artauction.auth.jwt.JwtService

class JwtAuthenticationFilterTest {

    @MockK
    private lateinit var userDetailsService: CustomUserDetailsServiceImpl

    @MockK
    private lateinit var jwtService: JwtService

    @MockK
    private lateinit var exchange: ServerWebExchange

    @MockK
    private lateinit var filterChain: WebFilterChain

    @MockK
    private lateinit var userDetails: UserDetails

    @InjectMockKs
    private lateinit var jwtAuthenticationFilter: JwtAuthenticationFilter

    @Test
    fun `should not authenticate when Authorization header is null`() {
        // GIVEN
        every { exchange.request.headers.getFirst(HEADER_AUTHORIZATION) } returns null
        every { filterChain.filter(exchange) } returns Mono.empty()

        // WHEN
        val result = jwtAuthenticationFilter.filter(exchange, filterChain)

        // THEN
        result.test()
            .verifyComplete()
        verify { filterChain.filter(exchange) }
        assertNull(ReactiveSecurityContextHolder.getContext().block()?.authentication)
    }

    @Test
    fun `should not authenticate when Authorization header does not start with Bearer`() {
        // GIVEN
        every { exchange.request.headers.getFirst(HEADER_AUTHORIZATION) } returns getRandomString()
        every { filterChain.filter(exchange) } returns Mono.empty()

        // WHEN
        val result = jwtAuthenticationFilter.filter(exchange, filterChain)

        // THEN
        result.test()
            .verifyComplete()
        assertNull(ReactiveSecurityContextHolder.getContext().block()?.authentication)
        verify { filterChain.filter(exchange) }
    }

    @Test
    fun `should authenticate when valid token is provided`() {
        // GIVEN
        val token = getRandomString()
        val email = getRandomEmail()

        every { exchange.request.headers.getFirst(HEADER_AUTHORIZATION) } returns "$HEADER_BEARER_PREFIX$token"
        every { jwtService.extractEmail(token) } returns email
        every { userDetailsService.findByUsername(email) } returns Mono.just(userDetails)
        every { jwtService.isValid(token, userDetails) } returns true
        every { userDetails.authorities } returns listOf()
        every { userDetails.username } returns email
        every { filterChain.filter(exchange) } returns Mono.empty()

        // WHEN
        val result = jwtAuthenticationFilter.filter(exchange, filterChain)

        // THEN
        result.test()
            .then {
                ReactiveSecurityContextHolder.getContext()
                    .map { it.authentication }
                    .doOnNext { authentication ->
                        assertNotNull(authentication)
                        assertEquals(email, authentication.name)
                    }
            }
            .verifyComplete()
        verify { filterChain.filter(exchange) }
    }

    @Test
    fun `should not authenticate when invalid token is provided`() {
        // GIVEN
        val token = getRandomString()
        val email = getRandomEmail()

        every { exchange.request.headers.getFirst(HEADER_AUTHORIZATION) } returns "$HEADER_BEARER_PREFIX$token"
        every { jwtService.extractEmail(token) } returns email
        every { userDetailsService.findByUsername(email) } returns Mono.just(userDetails)
        every { jwtService.isValid(token, userDetails) } returns false
        every { filterChain.filter(exchange) } returns Mono.empty()

        // WHEN
        val result = jwtAuthenticationFilter.filter(exchange, filterChain)

        // THEN
        result.test()
            .then {
                ReactiveSecurityContextHolder.getContext()
                    .map { it.authentication }
                    .doOnNext { authentication ->
                        assertNull(authentication)
                    }
            }
            .verifyComplete()
    }

    companion object {
        private const val HEADER_AUTHORIZATION = "Authorization"
        private const val HEADER_BEARER_PREFIX = "Bearer "
    }
}
