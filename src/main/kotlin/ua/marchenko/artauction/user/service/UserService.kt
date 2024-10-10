package ua.marchenko.artauction.user.service

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ua.marchenko.artauction.user.model.MongoUser

interface UserService {
    fun getAll(page: Int = 0, limit: Int = 10): Flux<MongoUser>
    fun getById(id: String): Mono<MongoUser>
    fun getByEmail(email: String): Mono<MongoUser>
}
