package ua.marchenko.artauction.service.user

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import ua.marchenko.artauction.dto.user.UserRequest
import ua.marchenko.artauction.dto.user.UserResponse
import ua.marchenko.artauction.exception.type.user.UserNotFoundException
import ua.marchenko.artauction.mapper.user.toUser
import ua.marchenko.artauction.mapper.user.toUserResponse
import ua.marchenko.artauction.repository.user.MongoUserRepository

@Service
class UserServiceImpl(
//    val userRepository: UserRepository,
    private val mongoUserRepository: MongoUserRepository
): UserService {

    override fun findAll(): List<UserResponse> {
        return mongoUserRepository.findAll().map { it.toUserResponse() }
    }

    override fun findById(id: String): UserResponse {
        return mongoUserRepository.findByIdOrNull(id)?.toUserResponse() ?: throwUserNotFoundException(id)
    }

    override fun save(userRequest: UserRequest): UserResponse {
        return mongoUserRepository.save(userRequest.toUser()).toUserResponse()
    }

    private fun throwUserNotFoundException(userId: String): Nothing {
        throw UserNotFoundException(userId)
    }

}