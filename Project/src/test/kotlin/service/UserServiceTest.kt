package service

import application.entity.User
import application.exception.BadRequestException
import application.exception.UserNotFoundException
import application.repository.UserRepository
import application.request.AuthoriseRequest
import application.service.JwtService
import application.service.UserService
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertThrows
import org.springframework.security.crypto.password.PasswordEncoder

class UserServiceTest {

    private val userRepository = mockk<UserRepository>()
    private val passwordEncoder = mockk<PasswordEncoder>()
    private val jwtService = mockk<JwtService>()
    private val userService = UserService(userRepository, passwordEncoder, jwtService)

    @Test
    fun `аунтификация пользователя, пользователя нет в базе`() {
        val request = AuthoriseRequest(
            login = "killer2000",
            password = "qwerty123456!"
        )

        every { userRepository.findByLogin(request.login) } returns null

        val exception = assertThrows(UserNotFoundException::class.java) {
            userService.authUser(request)
        }

        exception.message shouldBe "User not found"
    }

    @Test
    fun `аунтификация пользователя, пароли не совпадают`() {
        val request = AuthoriseRequest(
            login = "killer2000",
            password = "qwerty123456!"
        )

        every { passwordEncoder.encode(any()) } answers {
            firstArg()
        }
        every { userRepository.findByLogin(request.login) } returns User(
            login = "killer2000",
            password = passwordEncoder.encode("pupupu!2121"),
        )
        every { passwordEncoder.matches(any(), any()) } answers {
            firstArg<String>() == secondArg<String>()
        }

        val exception = assertThrows(BadRequestException::class.java) {
            userService.authUser(request)
        }

        exception.message shouldBe "Password is incorrect"
    }

    @Test
    fun `аунтификация пользователя, корректная`() {
        val request = AuthoriseRequest(
            login = "killer2000",
            password = "qwerty123456!"
        )

        every { passwordEncoder.encode(any()) } answers {
            firstArg()
        }
        every { userRepository.findByLogin(request.login) } returns User(
            login = "killer2000",
            password = passwordEncoder.encode("qwerty123456!"),
        )
        every { passwordEncoder.matches(any(), any()) } answers {
            firstArg<String>() == secondArg<String>()
        }
        every { jwtService.generateToken(claims = any<Map<String?, Any?>>(), subject = any<String>()) } returns "ok"
        every { userRepository.save(any()) } answers {
            firstArg()
        }

        userService.authUser(request)

        verify(exactly = 1) { jwtService.generateToken(claims = any<Map<String?, Any?>>(), subject = any<String>()) }
        verify(exactly = 1) { userRepository.save(any()) }
    }
}