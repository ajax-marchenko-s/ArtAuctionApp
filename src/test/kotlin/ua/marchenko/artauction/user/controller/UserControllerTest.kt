package ua.marchenko.artauction.user.controller

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import ua.marchenko.artauction.common.user.getRandomUser
import ua.marchenko.artauction.user.exception.UserNotFoundException
import ua.marchenko.artauction.user.mapper.toUserResponse
import ua.marchenko.artauction.user.service.UserService
import kotlin.test.Test

class UserControllerTest {

    private val mockUserService: UserService = mock(UserService::class.java)
    private val userController: UserController = UserController(mockUserService)

    @Test
    fun `getAllUsers should return a list of UserResponse`() {
        val users = listOf(getRandomUser())
        `when`(mockUserService.getAll()).thenReturn(users)
        val result = userController.getAllUsers()
        assertEquals(1, result.size)
        assertEquals(users[0].toUserResponse(), result[0])
    }

    @Test
    fun `getAllUsers should return an empty list if there are no user`() {
        `when`(mockUserService.getAll()).thenReturn(listOf())
        val result = userController.getAllUsers()
        assertEquals(0, result.size)
    }

    @Test
    fun `getUserById should return user with given id if auction with this id exists`() {
        val id = "1"
        val user = getRandomUser(id = id)
        `when`(mockUserService.getById(id)).thenReturn(user)
        val result = userController.getUserById(id)
        assertEquals(user.toUserResponse(), result)
    }

    @Test
    fun `getUserById should throw UserNotFoundException if there is no user with this id`() {
        val id = "1"
        `when`(mockUserService.getById(id)).thenThrow(UserNotFoundException(id))
        assertThrows<UserNotFoundException> { userController.getUserById(id) }
    }

}
