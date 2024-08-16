package ua.marchenko.artauction.user.service

import ua.marchenko.artauction.user.model.User

interface UserService {
    fun findAll(): List<User>
    fun findById(id: String): User
    fun findByEmail(email: String): User
}