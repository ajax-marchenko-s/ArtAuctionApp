package ua.marchenko.artauction.domainservice.user.domain

data class CreateUser(
    val name: String,
    val lastName: String,
    val email: String,
) {
    companion object
}
