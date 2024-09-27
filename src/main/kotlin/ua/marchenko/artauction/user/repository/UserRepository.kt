package ua.marchenko.artauction.user.repository

import ua.marchenko.artauction.user.model.MongoUser

interface UserRepository {
    fun save(user: MongoUser): MongoUser
    fun findById(id: String): MongoUser?
    fun findByEmail(email: String): MongoUser?
    fun findAll(page: Int = 0, limit: Int = 10): List<MongoUser>
    fun existsByEmail(email: String): Boolean
}
