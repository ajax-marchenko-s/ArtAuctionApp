package ua.marchenko.artauction.auth.data

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import ua.marchenko.artauction.user.enums.Role
import org.springframework.security.core.authority.SimpleGrantedAuthority

import java.util.Collections

class CustomUserDetails(
    private val username: String,
    private val password: String,
    private val role: Role
) : UserDetails {

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> =
        Collections.singletonList(SimpleGrantedAuthority(role.toString()))

    override fun getPassword(): String = password

    override fun getUsername(): String = username

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CustomUserDetails

        if (username != other.username) return false
        if (password != other.password) return false
        if (role != other.role) return false

        return true
    }

    override fun hashCode(): Int {
        var result = username.hashCode()
        result = 31 * result + password.hashCode()
        result = 31 * result + role.hashCode()
        return result
    }

}
