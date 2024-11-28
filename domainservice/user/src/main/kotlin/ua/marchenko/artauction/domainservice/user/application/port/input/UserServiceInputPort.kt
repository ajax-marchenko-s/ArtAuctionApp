package ua.marchenko.artauction.domainservice.user.application.port.input

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ua.marchenko.artauction.domainservice.user.domain.User

interface UserServiceInputPort {
    fun getAll(page: Int = 0, limit: Int = 10): Flux<User>
    fun getById(id: String): Mono<User>
    fun getByEmail(email: String): Mono<User>
    fun save(user: User): Mono<User>
    fun existById(id: String): Mono<Boolean>
}
