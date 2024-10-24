package ua.marchenko.artauction.user.repository.impl

import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.Fields
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ua.marchenko.artauction.user.model.MongoUser
import ua.marchenko.artauction.user.repository.UserRepository
import ua.marchenko.core.user.enums.Role

@Repository
internal class MongoUserRepository(
    private val reactiveMongoTemplate: ReactiveMongoTemplate,
) : UserRepository {

    override fun save(user: MongoUser): Mono<MongoUser> = reactiveMongoTemplate.save(user)

    override fun findById(id: String): Mono<MongoUser> {
        val query = Query.query(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(id))
        return reactiveMongoTemplate.findOne(query, MongoUser::class.java)
    }

    override fun findByEmail(email: String): Mono<MongoUser> {
        val query = Query(Criteria.where(MongoUser::email.name).isEqualTo(email))
        return reactiveMongoTemplate.findOne(query, MongoUser::class.java)
    }

    override fun findAll(page: Int, limit: Int): Flux<MongoUser> {
        val skip = page * limit
        val query = Query().skip(skip.toLong()).limit(limit)
        return reactiveMongoTemplate.find(query, MongoUser::class.java)
    }

    override fun existsByEmail(email: String): Mono<Boolean> {
        val query = Query(Criteria.where(MongoUser::email.name).isEqualTo(email))
        return reactiveMongoTemplate.exists(query, MongoUser::class.java)
    }

    override fun findByIdAndRole(id: String, role: Role): Mono<MongoUser> {
        val query =
            Query.query(Criteria.where(MongoUser::id.name).isEqualTo(id).and(MongoUser::role.name).isEqualTo(role))
        return reactiveMongoTemplate.findOne(query, MongoUser::class.java)
    }
}
