package ua.marchenko.artauction.user.service

import org.springframework.stereotype.Service
import ua.marchenko.artauction.user.exception.UserNotFoundException
import ua.marchenko.artauction.user.repository.UserRepository

@Service
class UserServiceImpl(private val userRepository: UserRepository) : UserService {

    override fun getAll() = userRepository.findAll()

    override fun getById(id: String) =
        userRepository.findById(id) ?: throw UserNotFoundException(value = id)

    override fun getByEmail(email: String) =
        userRepository.findByEmail(email) ?: throw UserNotFoundException(value = email, field = "email")

}
