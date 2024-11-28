package ua.marchenko.artauction.domainservice.user.infrastructure.mongo.repository

import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.Fields
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ua.marchenko.artauction.domainservice.user.application.port.output.UserRepositoryOutputPort
import ua.marchenko.artauction.domainservice.user.domain.User
import ua.marchenko.artauction.domainservice.user.infrastructure.mongo.entity.MongoUser
import ua.marchenko.artauction.domainservice.user.infrastructure.mongo.mapper.toDomain
import ua.marchenko.artauction.domainservice.user.infrastructure.mongo.mapper.toMongo

@Repository
class MongoUserRepository(
    private val reactiveMongoTemplate: ReactiveMongoTemplate,
) : UserRepositoryOutputPort {

    override fun save(user: User): Mono<User> = reactiveMongoTemplate.save(user.toMongo())
        .map { it.toDomain() }

    override fun findById(id: String): Mono<User> {
        val query = Query.query(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(id))
        return reactiveMongoTemplate.findOne(query, MongoUser::class.java)
            .map { it.toDomain() }
    }

    override fun findByEmail(email: String): Mono<User> {
        val query = Query(Criteria.where(MongoUser::email.name).isEqualTo(email))
        return reactiveMongoTemplate.findOne(query, MongoUser::class.java)
            .map { it.toDomain() }
    }

    override fun findAll(page: Int, limit: Int): Flux<User> {
        val skip = page * limit
        val query = Query().skip(skip.toLong()).limit(limit)
        return reactiveMongoTemplate.find(query, MongoUser::class.java)
            .map { it.toDomain() }
    }

    override fun existsById(id: String): Mono<Boolean> {
        val query = Query(Criteria.where(MongoUser::id.name).isEqualTo(id))
        return reactiveMongoTemplate.exists(query, MongoUser::class.java)
    }
}
