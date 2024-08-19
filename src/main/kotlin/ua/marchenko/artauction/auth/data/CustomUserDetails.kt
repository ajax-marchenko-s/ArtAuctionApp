package ua.marchenko.artauction.auth.data

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import ua.marchenko.artauction.user.enums.Role
import org.springframework.security.core.authority.SimpleGrantedAuthority

import java.util.Collections

data class CustomUserDetails(
    private val username: String,
    private val password: String,
    private val role: Role
) : UserDetails {

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> =
        Collections.singletonList(SimpleGrantedAuthority(role.toString()))

    override fun getPassword(): String = password

    override fun getUsername(): String = username
}
