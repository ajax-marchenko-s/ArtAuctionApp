package ua.marchenko.artauction.auth.jwt

import org.springframework.security.core.userdetails.UserDetails

interface JwtService {

    fun generate(userDetails: UserDetails, additionalClaims: Map<String, Any> = emptyMap()): String

    fun isValid(token: String, userDetails: UserDetails): Boolean

    fun extractEmail(token: String): String?

    fun isExpired(token: String): Boolean
}
