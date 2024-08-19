package ua.marchenko.artauction.user.repository

import ua.marchenko.artauction.user.model.User

interface UserRepository {
    fun save(user: User): User
    fun getByIdOrNull(id: String): User?
    fun getByEmailOrNull(email: String): User?
    fun getAll(): List<User>
    fun existsByEmail(email: String): Boolean
}
