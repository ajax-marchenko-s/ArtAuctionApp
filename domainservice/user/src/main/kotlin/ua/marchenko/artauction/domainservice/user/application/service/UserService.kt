package ua.marchenko.artauction.domainservice.user.application.service

import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import ua.marchenko.artauction.domainservice.user.application.exception.UserAlreadyExistsException
import ua.marchenko.artauction.domainservice.user.application.port.input.UserServiceInputPort
import ua.marchenko.artauction.core.user.exception.UserNotFoundException
import ua.marchenko.artauction.domainservice.user.application.port.output.UserRepositoryOutputPort
import ua.marchenko.artauction.domainservice.user.domain.User

@Service
class UserService(private val userRepository: UserRepositoryOutputPort) :
    UserServiceInputPort {

    override fun getAll(page: Int, limit: Int): Flux<User> = userRepository.findAll(page, limit)

    override fun getById(id: String): Mono<User> =
        userRepository.findById(id).switchIfEmpty { Mono.error(UserNotFoundException(value = id)) }

    override fun getByEmail(email: String): Mono<User> =
        userRepository.findByEmail(email)
            .switchIfEmpty { Mono.error(UserNotFoundException(value = email, field = "email")) }

    override fun save(user: User): Mono<User> =
        userRepository.save(user)
            .onErrorMap(DuplicateKeyException::class.java) { UserAlreadyExistsException() }

    override fun existById(id: String): Mono<Boolean> = userRepository.existsById(id)
}
