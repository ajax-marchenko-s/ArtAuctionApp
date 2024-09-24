package ua.marchenko.artauction.user.repository

import getRandomEmail
import getRandomObjectId
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
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
        assertEquals(user.email, savedUser.email)
    }

    @Test
    fun `should find user by id when this user exists`() {
        // GIVEN
        val savedUser = userRepository.save(MongoUser.random(id = null))

        // WHEN
        val foundUser = userRepository.findById(savedUser.id.toString())

        // THEN
        assertEquals(savedUser.id, foundUser?.id)
    }

    @Test
    fun `should return null when there is no user with this id`() {
        // WHEN
        val result = userRepository.findById(getRandomObjectId().toHexString())

        // THEN
        assertNull(result, "Found user must be null")
    }

    @Test
    fun `should return true when user with given email exists`() {
        // GIVEN
        val savedUser = userRepository.save(MongoUser.random(id = null))

        // WHEN
        val result = userRepository.existsByEmail(savedUser.email!!)

        // THEN
        assertTrue(result, "User with given email must exist")
    }

    @Test
    fun `should return false when user with given email does not exists`() {
        // WHEN
        val result = userRepository.existsByEmail(getRandomEmail())

        // THEN
        assertFalse(result, "User with given email must not exist")
    }

    @Test
    fun `should return user when user with given email exists`() {
        // GIVEN
        val savedUser = userRepository.save(MongoUser.random(id = null))

        // WHEN
        val foundUser = userRepository.findByEmail(savedUser.email!!)

        // THEN
        assertEquals(savedUser.email, foundUser?.email)
    }

    @Test
    fun `should return null when user with given email doesnt exists`() {
        // WHEN
        val result = userRepository.findByEmail(getRandomEmail())

        // THEN
        assertNull(result, "Found user must be null")
    }

    @Test
    fun `should return all users when they are exists`() {
        // GIVEN
        val users = listOf(MongoUser.random(id = null), MongoUser.random(id = null))
        users.forEach { user -> userRepository.save(user) }

        // WHEN
        val result = userRepository.findAll()

        // THEN
        assertTrue(result.size >= users.size, "Size of list must be at least ${users.size}")
        users.forEach { user ->
            assertTrue(result.any { it.email == user.email }, "User with email ${user.email} must be found")
        }
    }
}
