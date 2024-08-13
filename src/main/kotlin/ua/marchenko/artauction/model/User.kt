package ua.marchenko.artauction.model

import ua.marchenko.artauction.enums.user.Role
import java.util.*

data class User(
    val id: String = UUID.randomUUID().toString(),
    val username: String,
    val email: String,
    val password: String,
    val role: Role
)
