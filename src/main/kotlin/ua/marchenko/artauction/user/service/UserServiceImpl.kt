package ua.marchenko.artauction.user.service

import org.springframework.stereotype.Service
import ua.marchenko.artauction.user.exception.UserNotFoundException
import ua.marchenko.artauction.user.repository.UserRepository

@Service
class UserServiceImpl(private val userRepository: UserRepository) : UserService {

    override fun getAll() = userRepository.findAll()

    override fun getById(id: String) = userRepository.findById(id) ?: throwUserNotFoundException(value = id)

    override fun getByEmail(email: String) =
        userRepository.findByEmail(email) ?: throwUserNotFoundException(value = email, field = "email")

    private fun throwUserNotFoundException(value: String, field: String): Nothing =
        throw UserNotFoundException(value, field)

    private fun throwUserNotFoundException(value: String): Nothing = throw UserNotFoundException(value)
}
