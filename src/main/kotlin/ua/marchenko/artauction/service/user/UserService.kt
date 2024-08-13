package ua.marchenko.artauction.service.user

import ua.marchenko.artauction.dto.user.UserRequest
import ua.marchenko.artauction.dto.user.UserResponse

interface UserService {

    fun findAll(): List<UserResponse>
    fun findById(id: String): UserResponse
    fun save(userRequest: UserRequest): UserResponse
}