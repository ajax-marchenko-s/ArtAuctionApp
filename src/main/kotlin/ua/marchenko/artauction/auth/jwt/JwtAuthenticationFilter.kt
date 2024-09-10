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
    private val jwtService: JwtService,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader: String? = request.getHeader(HEADER_AUTHORIZATION)
        if (authHeader == null || !authHeader.startsWith(HEADER_BEARER_PREFIX)) {
            filterChain.doFilter(request, response)
            return
        }
        val jwtToken = authHeader.extractTokenValue()
        val email = jwtService.extractEmail(jwtToken)
        if (email != null && SecurityContextHolder.getContext().authentication == null) {
            val foundUser = userDetailsService.loadUserByUsername(email)
            if (jwtService.isValid(jwtToken, foundUser))
                updateContext(foundUser, request)
            filterChain.doFilter(request, response)
        }
    }

    private fun String.extractTokenValue() = substringAfter(HEADER_BEARER_PREFIX)

    private fun updateContext(foundUser: UserDetails, request: HttpServletRequest) {
        val authToken = UsernamePasswordAuthenticationToken(foundUser, null, foundUser.authorities)
        authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
        SecurityContextHolder.getContext().authentication = authToken
    }

    companion object {
        private const val HEADER_AUTHORIZATION = "Authorization"
        private const val HEADER_BEARER_PREFIX = "Bearer "
    }
}
