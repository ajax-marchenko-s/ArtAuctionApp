package ua.marchenko.artauction.domainservice.user.domain

data class User(
    val id: String?,
    val name: String,
    val lastName: String,
    val email: String,
) {
    companion object
}
