package ua.marchenko.artauction.auth.filter

import getRandomEmail
import getRandomString
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kotlin.test.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.security.core.context.SecurityContextHolder
import ua.marchenko.artauction.auth.service.CustomUserDetailsServiceImpl
import org.junit.jupiter.api.Assertions.assertNull
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import ua.marchenko.artauction.auth.jwt.JwtAuthenticationFilter
import ua.marchenko.artauction.auth.jwt.JwtService

class JwtAuthenticationFilterTest {

    @MockK
    private lateinit var userDetailsService: CustomUserDetailsServiceImpl

    @MockK
    private lateinit var jwtService: JwtService

    @MockK
    private lateinit var request: HttpServletRequest

    @MockK
    private lateinit var response: HttpServletResponse

    @MockK
    private lateinit var filterChain: FilterChain

    @MockK
    private lateinit var userDetails: UserDetails

    @InjectMockKs
    private lateinit var jwtAuthenticationFilter: JwtAuthenticationFilterForTest

    @Test
    fun `should not authenticate when Authorization header is null`() {
        // GIVEN
        every { request.getHeader(HEADER_AUTHORIZATION) } returns null
        every { filterChain.doFilter(request, response) } just Runs

        // WHEN
        jwtAuthenticationFilter.doFilterInternalTest(request, response, filterChain)

        // THEN
        verify { filterChain.doFilter(request, response) }
        assertNull(SecurityContextHolder.getContext().authentication)
    }

    @Test
    fun `should not authenticate when Authorization header does not start with Bearer`() {
        // GIVEN
        every { request.getHeader(HEADER_AUTHORIZATION) } returns getRandomString()
        every { filterChain.doFilter(request, response) } just Runs

        // WHEN
        jwtAuthenticationFilter.doFilterInternalTest(request, response, filterChain)

        // THEN
        assertNull(SecurityContextHolder.getContext().authentication)
        verify { filterChain.doFilter(request, response) }
    }

    @Test
    fun `should authenticate when valid token is provided`() {
        // GIVEN
        val token = getRandomString()
        val email = getRandomEmail()
        val remoteAddr = "127.0.0.1"

        every { request.getHeader(HEADER_AUTHORIZATION) } returns "$HEADER_BEARER_PREFIX$token"
        every { jwtService.extractEmail(token) } returns email
        every { userDetailsService.loadUserByUsername(email) } returns userDetails
        every { jwtService.isValid(token, userDetails) } returns true
        every { userDetails.authorities } returns listOf()
        every { WebAuthenticationDetailsSource().buildDetails(request) } returns null
        every { request.remoteAddr } returns remoteAddr
        every { filterChain.doFilter(request, response) } just Runs

        // WHEN
        jwtAuthenticationFilter.doFilterInternalTest(request, response, filterChain)

        // THEN
        assertNotNull(SecurityContextHolder.getContext().authentication)
        verify { filterChain.doFilter(request, response) }
    }

    @Test
    fun `should not authenticate when invalid token is provided`() {
        // GIVEN
        val token = getRandomString()
        val email = getRandomEmail()
        val remoteAddr = "127.0.0.1"

        every { request.getHeader(HEADER_AUTHORIZATION) } returns "$HEADER_BEARER_PREFIX$token"
        every { jwtService.extractEmail(token) } returns email
        every { userDetailsService.loadUserByUsername(email) } returns userDetails
        every { jwtService.isValid(token, userDetails) } returns false
        every { userDetails.authorities } returns listOf()
        every { WebAuthenticationDetailsSource().buildDetails(request) } returns null
        every { request.remoteAddr } returns remoteAddr
        every { filterChain.doFilter(request, response) } just Runs

        // WHEN
        jwtAuthenticationFilter.doFilterInternalTest(request, response, filterChain)

        // THEN
        assertNull(SecurityContextHolder.getContext().authentication)
        verify { filterChain.doFilter(request, response) }
    }

    class JwtAuthenticationFilterForTest(
        userDetailsService: CustomUserDetailsServiceImpl,
        jwtService: JwtService,
    ) : JwtAuthenticationFilter(
        userDetailsService,
        jwtService,
    ) {
        fun doFilterInternalTest(req: HttpServletRequest, res: HttpServletResponse, filterChain: FilterChain) =
            doFilterInternal(req, res, filterChain)
    }

    companion object {
        private const val HEADER_AUTHORIZATION = "Authorization"
        private const val HEADER_BEARER_PREFIX = "Bearer "
    }
}
