package ua.marchenko.artauction.domainservice.user.application.port.output

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ua.marchenko.artauction.domainservice.user.domain.User

interface UserRepositoryOutputPort {
    fun save(user: User): Mono<User>
    fun findById(id: String): Mono<User>
    fun findByEmail(email: String): Mono<User>
    fun findAll(page: Int = 0, limit: Int = 10): Flux<User>
    fun existsById(id: String): Mono<Boolean>
}
