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
import ua.marchenko.artauction.common.getRandomString

class UserControllerTest {

    private val mockUserService: UserService = mock(UserService::class.java)
    private val userController: UserController = UserController(mockUserService)

    @Test
    fun `getAllUsers should return a list of UserResponse`() {
        //GIVEN
        val users = listOf(getRandomUser())
        `when`(mockUserService.getAll()).thenReturn(users)

        //WHEN
        val result = userController.getAllUsers()

        //THEN
        assertEquals(1, result.size)
        assertEquals(users[0].toUserResponse(), result[0])
    }

    @Test
    fun `getAllUsers should return an empty list if there are no user`() {
        //GIVEN
        `when`(mockUserService.getAll()).thenReturn(listOf())

        //WHEN
        val result = userController.getAllUsers()

        //THEN
        assertEquals(0, result.size)
    }

    @Test
    fun `getUserById should return user with given id if auction with this id exists`() {
        //GIVEN
        val id = getRandomString()
        val user = getRandomUser(id = id)

        `when`(mockUserService.getById(id)).thenReturn(user)

        //WHEN
        val result = userController.getUserById(id)

        //THEN
        assertEquals(user.toUserResponse(), result)
    }

    @Test
    fun `getUserById should throw UserNotFoundException if there is no user with this id`() {
        //GIVEN
        val id = getRandomString()
        `when`(mockUserService.getById(id)).thenThrow(UserNotFoundException(id))

        //WHEN-THEN
        assertThrows<UserNotFoundException> { userController.getUserById(id) }
    }
}
