package ua.marchenko.artauction.user.controller

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import ua.marchenko.artauction.user.exception.UserNotFoundException
import ua.marchenko.artauction.user.mapper.toUserResponse
import ua.marchenko.artauction.user.service.UserService
import kotlin.test.Test
import getRandomString
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.bson.types.ObjectId
import ua.marchenko.artauction.user.model.MongoUser
import user.random

class UserControllerTest {

    @MockK
    private lateinit var mockUserService: UserService

    @InjectMockKs
    private lateinit var userController: UserController

    @Test
    fun `should return a list of UserResponse`() {
        //GIVEN
        val users = listOf(MongoUser.random())
        every { mockUserService.getAll() } returns users

        //WHEN
        val result = userController.getAllUsers(1, 10)

        //THEN
        assertEquals(1, result.size)
        assertEquals(users[0].toUserResponse(), result[0])
    }

    @Test
    fun `should return an empty list when there are no user`() {
        //GIVEN
        every { mockUserService.getAll() } returns emptyList()

        //WHEN
        val result = userController.getAllUsers(1, 10)

        //THEN
        assertEquals(0, result.size)
    }

    @Test
    fun `should return user with given id when auction with this id exists`() {
        //GIVEN
        val id = ObjectId().toHexString()
        val user = MongoUser.random(id = id)

        every { mockUserService.getById(id) } returns user

        //WHEN
        val result = userController.getUserById(id)

        //THEN
        assertEquals(user.toUserResponse(), result)
    }

    @Test
    fun `should throw UserNotFoundException when there is no user with this id`() {
        //GIVEN
        val id = getRandomString()
        every { mockUserService.getById(id) } throws UserNotFoundException(id)

        //WHEN //THEN
        assertThrows<UserNotFoundException> { userController.getUserById(id) }
    }
}
