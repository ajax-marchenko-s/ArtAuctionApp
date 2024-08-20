package ua.marchenko.artauction.user.repository

import org.bson.types.ObjectId
import ua.marchenko.artauction.user.model.User

interface UserRepository {
    fun save(user: User): User
    fun findById(id: ObjectId): User?
    fun findByEmail(email: String): User?
    fun findAll(): List<User>
    fun existsByEmail(email: String): Boolean
}
