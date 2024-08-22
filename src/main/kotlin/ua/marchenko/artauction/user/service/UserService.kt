package ua.marchenko.artauction.user.service

import ua.marchenko.artauction.user.model.User

interface UserService {
    fun getAll(): List<User>
    fun getById(id: String): User
    fun getByEmail(email: String): User
}
