package ua.marchenko.artauction.auth.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import ua.marchenko.artauction.auth.service.CustomUserDetailsServiceImpl


@Component
class JwtAuthenticationFilter(
    private val userDetailsService: CustomUserDetailsServiceImpl,
    private val jwtService: JwtService
) : OncePerRequestFilter() {

    companion object {
        const val HEADER_AUTHORIZATION = "Authorization"
        const val HEADER_BEARER_PREFIX = "Bearer "
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader: String? = request.getHeader(HEADER_AUTHORIZATION)
        if (authHeader.doesNotContainBearerToken()) {
            filterChain.doFilter(request, response)
            return
        }
        val jwtToken = authHeader!!.extractTokenValue()
        val email = jwtService.extractEmail(jwtToken)
        if (email != null && SecurityContextHolder.getContext().authentication == null) {
            val foundUser = userDetailsService.loadUserByUsername(email)
            if (jwtService.isValid(jwtToken, foundUser))
                updateContext(foundUser, request)
            filterChain.doFilter(request, response)
        }
    }

    private fun String?.doesNotContainBearerToken() = this == null || !this.startsWith(HEADER_BEARER_PREFIX)

    private fun String.extractTokenValue() = this.substringAfter(HEADER_BEARER_PREFIX)

    private fun updateContext(foundUser: UserDetails, request: HttpServletRequest) {
        val authToken = UsernamePasswordAuthenticationToken(foundUser, null, foundUser.authorities)
        authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
        SecurityContextHolder.getContext().authentication = authToken
    }
}
