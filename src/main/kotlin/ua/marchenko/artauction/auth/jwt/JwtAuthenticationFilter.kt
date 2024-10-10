package ua.marchenko.artauction.auth.jwt

import org.springframework.stereotype.Component
import ua.marchenko.artauction.auth.service.CustomUserDetailsServiceImpl
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class JwtAuthenticationFilter(
    private val userDetailsService: CustomUserDetailsServiceImpl,
    private val jwtService: JwtService,
) : WebFilter {

    @Suppress("ForbiddenVoid", "ReturnCount")
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val authHeader = exchange.request.headers.getFirst(HEADER_AUTHORIZATION)

        if (authHeader == null || !authHeader.startsWith(HEADER_BEARER_PREFIX)) {
            return chain.filter(exchange)
        }

        val jwtToken = authHeader.extractTokenValue()
        val email = jwtService.extractEmail(jwtToken)

        if (email != null && SecurityContextHolder.getContext().authentication == null) {
            return userDetailsService.findByUsername(email)
                .filter { jwtService.isValid(jwtToken, it) }
                .flatMap { userDetails ->
                    chain.filter(exchange).contextWrite(
                        ReactiveSecurityContextHolder.withAuthentication(
                            UsernamePasswordAuthenticationToken(userDetails.username, null, userDetails.authorities)
                        )
                    )
                }
        }
        return chain.filter(exchange)
    }

    private fun String.extractTokenValue() = substringAfter(HEADER_BEARER_PREFIX)

    companion object {
        private const val HEADER_AUTHORIZATION = "Authorization"
        private const val HEADER_BEARER_PREFIX = "Bearer "
    }
}

