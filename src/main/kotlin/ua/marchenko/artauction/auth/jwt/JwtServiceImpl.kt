package ua.marchenko.artauction.auth.jwt

import org.springframework.beans.factory.annotation.Value

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.Date

@Service
class JwtServiceImpl(
    @Value("\${security.jwt.key}")
    private val secret: String,

    @Value("\${security.jwt.expiration}")
    private val expiration: Int
) : JwtService {

    private val secretKey = Keys.hmacShaKeyFor(
        secret.toByteArray()
    )

    override fun generate(
        userDetails: UserDetails,
        additionalClaims: Map<String, Any>
    ): String =
        Jwts.builder()
            .claims()
            .subject(userDetails.username)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis() + expiration))
            .add(additionalClaims)
            .and()
            .signWith(secretKey)
            .compact()

    override fun isValid(token: String, userDetails: UserDetails): Boolean {
        val email = extractEmail(token)
        return userDetails.username == email && !isExpired(token)
    }

    override fun extractEmail(token: String): String? =
        getAllClaims(token)
            .subject

    override fun isExpired(token: String): Boolean =
        getAllClaims(token)
            .expiration
            .before(Date(System.currentTimeMillis()))

    private fun getAllClaims(token: String): Claims {
        val parser = Jwts.parser()
            .verifyWith(secretKey)
            .build()
        return parser
            .parseSignedClaims(token)
            .payload
    }
}
