package ua.marchenko.artauction.user.repository

import getRandomEmail
import kotlin.test.Test
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.beans.factory.annotation.Autowired
import reactor.kotlin.test.test
import ua.marchenko.artauction.common.AbstractBaseIntegrationTest
import ua.marchenko.artauction.user.model.MongoUser
import user.random

class UserRepositoryTest : AbstractBaseIntegrationTest {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `should save user`() {
        // GIVEN
        val user = MongoUser.random(id = null)

        // WHEN
        val savedUser = userRepository.save(user)

        // THEN
        savedUser.test()
            .assertNext { userFromMono -> assertEquals(user.copy(id = userFromMono.id), userFromMono) }
            .verifyComplete()
    }

    @Test
    fun `should find user by id when this user exists`() {
        // GIVEN
        val savedUser = userRepository.save(MongoUser.random(id = null)).block()

        // WHEN
        val foundUser = userRepository.findById(savedUser!!.id.toString())

        // THEN
        foundUser.test()
            .expectNext(savedUser)
            .verifyComplete()
    }

    @Test
    fun `should return empty when there is no user with this id`() {
        // WHEN
        val result = userRepository.findById(ObjectId().toHexString())

        // THEN
        result.test()
            .verifyComplete()
    }

    @Test
    fun `should return true when user with given email exists`() {
        // GIVEN
        val savedUser = userRepository.save(MongoUser.random(id = null)).block()

        // WHEN
        val result = userRepository.existsByEmail(savedUser!!.email!!)

        // THEN
        result.test()
            .assertNext { assertTrue(it, "User with email ${savedUser.email} must exist") }
            .verifyComplete()
    }

    @Test
    fun `should return false when user with given email does not exists`() {
        // WHEN
        val result = userRepository.existsByEmail(getRandomEmail())

        // THEN
        result.test()
            .assertNext { assertFalse(it, "User with given email must not exist") }
            .verifyComplete()
    }

    @Test
    fun `should return user when user with given email exists`() {
        // GIVEN
        val savedUser = userRepository.save(MongoUser.random(id = null)).block()

        // WHEN
        val foundUser = userRepository.findByEmail(savedUser!!.email!!)

        // THEN
        foundUser.test()
            .expectNext(savedUser)
            .verifyComplete()
    }

    @Test
    fun `should return Mono empty() when user with given email doesnt exists`() {
        // WHEN
        val result = userRepository.findByEmail(getRandomEmail())

        // THEN
        result.test()
            .verifyComplete()
    }

    @Test
    fun `should return all users when they are exists`() {
        // GIVEN
        val users = listOf(userRepository.save(MongoUser.random(id = null)).block())

        // WHEN
        val result = userRepository.findAll().collectList()

        // THEN
        result.test()
            .assertNext { assertTrue(it.containsAll(users), "Users $users must be found") }
            .verifyComplete()
    }
}
