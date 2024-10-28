package ua.marchenko.artauction.user.service

import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import ua.marchenko.artauction.user.exception.UserAlreadyExistsException
import ua.marchenko.core.user.exception.UserNotFoundException
import ua.marchenko.artauction.user.model.MongoUser
import ua.marchenko.artauction.user.repository.UserRepository

@Service
class UserServiceImpl(private val userRepository: UserRepository) : UserService {

    override fun getAll(page: Int, limit: Int): Flux<MongoUser> = userRepository.findAll(page, limit)

    override fun getById(id: String): Mono<MongoUser> =
        userRepository.findById(id).switchIfEmpty { Mono.error(UserNotFoundException(value = id)) }

    override fun getByEmail(email: String): Mono<MongoUser> =
        userRepository.findByEmail(email)
            .switchIfEmpty { Mono.error(UserNotFoundException(value = email, field = "email")) }

    override fun save(user: MongoUser): Mono<MongoUser> =
        userRepository.save(user)
            .onErrorMap(DuplicateKeyException::class.java) {
                UserAlreadyExistsException(userEmail = user.email ?: "")
            }
}
