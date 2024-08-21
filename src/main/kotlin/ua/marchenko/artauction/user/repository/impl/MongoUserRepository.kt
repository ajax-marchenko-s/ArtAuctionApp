package ua.marchenko.artauction.user.repository.impl

import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository
import ua.marchenko.artauction.user.model.User
import ua.marchenko.artauction.user.repository.UserRepository

@Repository
class MongoUserRepository(private val mongoTemplate: MongoTemplate) : UserRepository {

    override fun save(user: User) = mongoTemplate.save(user)

    override fun findById(id: String): User? {
        val query = Query.query(Criteria.where("id").isEqualTo(id))
        return mongoTemplate.findOne(query, User::class.java)
    }

    override fun findByEmail(email: String): User? {
        val query = Query(Criteria.where("email").isEqualTo(email))
        return mongoTemplate.findOne(query, User::class.java)
    }

    override fun findAll(): List<User> = mongoTemplate.findAll(User::class.java)

    override fun existsByEmail(email: String): Boolean {
        val query = Query(Criteria.where("email").isEqualTo(email))
        return mongoTemplate.exists(query, User::class.java)
    }
}
