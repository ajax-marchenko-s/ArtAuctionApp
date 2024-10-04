package ua.marchenko.artauction.user.service

import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ua.marchenko.artauction.user.exception.UserNotFoundException
import ua.marchenko.artauction.user.model.MongoUser
import ua.marchenko.artauction.user.repository.UserRepository

@Service
class UserServiceImpl(private val userRepository: UserRepository) : UserService {

    override fun getAll(page: Int, limit: Int): Flux<MongoUser> = userRepository.findAll(page, limit)

    override fun getById(id: String): Mono<MongoUser> =
        userRepository.findById(id).switchIfEmpty(Mono.error(UserNotFoundException(value = id)))

    override fun getByEmail(email: String): Mono<MongoUser> =
        userRepository.findByEmail(email)
            .switchIfEmpty(Mono.error(UserNotFoundException(value = email, field = "email")))
}
