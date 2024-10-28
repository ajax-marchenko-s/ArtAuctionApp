package ua.marchenko.artauction.user.repository

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ua.marchenko.artauction.user.model.MongoUser

interface UserRepository {
    fun save(user: MongoUser): Mono<MongoUser>
    fun findById(id: String): Mono<MongoUser>
    fun findByEmail(email: String): Mono<MongoUser>
    fun findAll(page: Int = 0, limit: Int = 10): Flux<MongoUser>
    fun existsByEmail(email: String): Mono<Boolean>
}
