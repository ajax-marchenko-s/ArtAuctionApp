package ua.marchenko.artauction.auth.controller.dto

data class AuthenticationRequest(
    val email: String,
    val password: String,
) {
    companion object
}
