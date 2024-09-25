package ua.marchenko.artauction.user.service

import ua.marchenko.artauction.user.model.MongoUser

interface UserService {
    fun getAll(page: Int = 0, limit: Int = 10): List<MongoUser>
    fun getById(id: String): MongoUser
    fun getByEmail(email: String): MongoUser
}
