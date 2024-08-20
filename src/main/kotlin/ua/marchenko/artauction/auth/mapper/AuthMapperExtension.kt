package ua.marchenko.artauction.auth.mapper

import org.springframework.security.core.userdetails.UserDetails
import ua.marchenko.artauction.auth.controller.dto.RegistrationRequest
import ua.marchenko.artauction.auth.data.CustomUserDetails
import ua.marchenko.artauction.user.enums.Role
import ua.marchenko.artauction.user.model.User

fun RegistrationRequest.toUser() = User(null, name, lastname, email, password, role)

fun User.toUserDetails(): UserDetails = CustomUserDetails(
    this.email ?: "unknown",
    this.password ?: "unknown",
    this.role ?: Role.UNKNOWN,
)

