package ua.marchenko.artauction.user.controller

import getRandomObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import ua.marchenko.artauction.user.exception.UserNotFoundException
import ua.marchenko.artauction.user.mapper.toUserResponse
import ua.marchenko.artauction.user.service.UserService
import kotlin.test.Test
import getRandomString
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.whenever
import ua.marchenko.artauction.user.model.User
import user.random

class UserControllerTest {

    private val mockUserService: UserService = mock(UserService::class.java)
    private val userController: UserController = UserController(mockUserService)

    @Test
    fun `getAllUsers should return a list of UserResponse`() {
        //GIVEN
        val users = listOf(User.random())
        whenever(mockUserService.getAll()) doReturn (users)

        //WHEN
        val result = userController.getAllUsers()

        //THEN
        assertEquals(1, result.size)
        assertEquals(users[0].toUserResponse(), result[0])
    }

    @Test
    fun `getAllUsers should return an empty list if there are no user`() {
        //GIVEN
        whenever(mockUserService.getAll()) doReturn (listOf())

        //WHEN
        val result = userController.getAllUsers()

        //THEN
        assertEquals(0, result.size)
    }

    @Test
    fun `getUserById should return user with given id if auction with this id exists`() {
        //GIVEN
        val id = getRandomObjectId().toString()
        val user = User.random(id = id)

        whenever(mockUserService.getById(id)) doReturn (user)

        //WHEN
        val result = userController.getUserById(id)

        //THEN
        assertEquals(user.toUserResponse(), result)
    }

    @Test
    fun `getUserById should throw UserNotFoundException if there is no user with this id`() {
        //GIVEN
        val id = getRandomString()
        whenever(mockUserService.getById(id)) doThrow (UserNotFoundException(id))

        //WHEN-THEN
        assertThrows<UserNotFoundException> { userController.getUserById(id) }
    }
}
