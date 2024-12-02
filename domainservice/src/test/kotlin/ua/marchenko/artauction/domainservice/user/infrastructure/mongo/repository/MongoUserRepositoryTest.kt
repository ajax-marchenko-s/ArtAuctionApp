package ua.marchenko.artauction.domainservice.user.infrastructure.mongo.repository

import kotlin.test.Test
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.beans.factory.annotation.Autowired
import reactor.core.publisher.Mono
import reactor.kotlin.test.test
import ua.marchenko.artauction.domainservice.utils.AbstractBaseIntegrationTest
import ua.marchenko.artauction.domainservice.user.application.port.output.UserRepositoryOutputPort
import ua.marchenko.artauction.domainservice.user.domain.CreateUser
import ua.marchenko.artauction.domainservice.user.domain.User
import ua.marchenko.artauction.domainservice.user.domain.random
import ua.marchenko.artauction.domainservice.user.getRandomEmail

class MongoUserRepositoryTest : AbstractBaseIntegrationTest {

    @Autowired
    private lateinit var mongoUserRepository: UserRepositoryOutputPort

    @Test
    fun `should save user`() {
        // GIVEN
        val createUser = CreateUser.random()
        val expectedUser = User(
            id = EMPTY_STRING,
            name = createUser.name,
            lastName = createUser.lastName,
            email = createUser.email,
        )

        // WHEN
        val savedUser: Mono<User> = mongoUserRepository.save(createUser)

        // THEN
        savedUser.test()
            .assertNext { userFromMono -> assertEquals(expectedUser.copy(id = userFromMono.id), userFromMono) }
            .verifyComplete()
    }

    @Test
    fun `should find user by id when this user exists`() {
        // GIVEN
        val savedUser = mongoUserRepository.save(CreateUser.random()).block()

        // WHEN
        val foundUser = mongoUserRepository.findById(savedUser!!.id)

        // THEN
        foundUser.test()
            .expectNext(savedUser)
            .verifyComplete()
    }

    @Test
    fun `should return empty when there is no user with this id`() {
        // WHEN
        val result = mongoUserRepository.findById(ObjectId().toHexString())

        // THEN
        result.test()
            .verifyComplete()
    }

    @Test
    fun `should return true when user with given id exists`() {
        // GIVEN
        val savedUser = mongoUserRepository.save(CreateUser.random()).block()

        // WHEN
        val result = mongoUserRepository.existsById(savedUser!!.id)

        // THEN
        result.test()
            .assertNext { assertTrue(it, "User with id ${savedUser.id} must exist") }
            .verifyComplete()
    }

    @Test
    fun `should return false when user with given id does not exists`() {
        // WHEN
        val result = mongoUserRepository.existsById(ObjectId().toHexString())

        // THEN
        result.test()
            .assertNext { assertFalse(it, "User with given id must not exist") }
            .verifyComplete()
    }

    @Test
    fun `should return user when user with given email exists`() {
        // GIVEN
        val savedUser = mongoUserRepository.save(CreateUser.random()).block()

        // WHEN
        val foundUser = mongoUserRepository.findByEmail(savedUser!!.email)

        // THEN
        foundUser.test()
            .expectNext(savedUser)
            .verifyComplete()
    }

    @Test
    fun `should return Mono empty() when user with given email doesnt exists`() {
        // WHEN
        val result = mongoUserRepository.findByEmail(getRandomEmail())

        // THEN
        result.test()
            .verifyComplete()
    }

    @Test
    fun `should return all users when they are exists`() {
        // GIVEN
        val users = listOf(mongoUserRepository.save(CreateUser.random()).block())

        // WHEN
        val result = mongoUserRepository.findAll(page = 0, limit = 100).collectList()

        // THEN
        result.test()
            .assertNext { assertTrue(it.containsAll(users), "Users $users must be found") }
            .verifyComplete()
    }

    companion object {
        private const val EMPTY_STRING = ""
    }
}
