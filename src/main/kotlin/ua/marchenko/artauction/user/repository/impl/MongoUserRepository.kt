package ua.marchenko.artauction.user.repository.impl

import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Fields
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository
import ua.marchenko.artauction.user.model.MongoUser
import ua.marchenko.artauction.user.repository.UserRepository

@Repository
internal class MongoUserRepository(private val mongoTemplate: MongoTemplate) : UserRepository {

    override fun save(user: MongoUser): MongoUser = mongoTemplate.save(user)

    override fun findById(id: String): MongoUser? {
        val query = Query.query(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(id))
        return mongoTemplate.findOne(query, MongoUser::class.java)
    }

    override fun findByEmail(email: String): MongoUser? {
        val query = Query(Criteria.where(MongoUser::email.name).isEqualTo(email))
        return mongoTemplate.findOne(query, MongoUser::class.java)
    }

    override fun findAll(page: Int, limit: Int): List<MongoUser> {
        val skip = (page - 1) * limit
        val query = Query().skip(skip.toLong()).limit(limit)
        return mongoTemplate.find(query, MongoUser::class.java)
    }

    override fun existsByEmail(email: String): Boolean {
        val query = Query(Criteria.where(MongoUser::email.name).isEqualTo(email))
        return mongoTemplate.exists(query, MongoUser::class.java)
    }
}
