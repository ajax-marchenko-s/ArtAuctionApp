package ua.marchenko.artauction.auth.mapper

import org.springframework.security.core.userdetails.UserDetails
import ua.marchenko.artauction.auth.controller.dto.RegistrationRequest
import ua.marchenko.artauction.auth.data.CustomUserDetails
import ua.marchenko.artauction.user.model.User

fun RegistrationRequest.toUser() = User(null, name, lastname, email, password, role)

fun User.toUserDetails(): UserDetails = CustomUserDetails(
    this.email ?: throwIllegalArgumentException("email"),
    this.password ?: throwIllegalArgumentException("password"),
    this.role ?: throwIllegalArgumentException("role")
)

private fun throwIllegalArgumentException(field: String): Nothing {
    throw IllegalArgumentException("User entity is in an invalid state: missing required field: $field")
}