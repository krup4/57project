package service

import application.entity.User
import application.exception.BadRequestException
import application.exception.UserIsAlreadyExistsException
import application.exception.UserNotFoundException
import application.repository.UserRepository
import application.request.AuthoriseRequest
import application.request.RegisterAdminRequest
import application.service.JwtService
import application.service.UserService
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Assertions.assertThrows
import org.springframework.security.crypto.password.PasswordEncoder
import application.response.StatusResponse
import io.mockk.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension

@ExtendWith(SystemStubsExtension::class)
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
    fun `аунтификация пользователя, регистрация не подтверждена`() {
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
            isRegistered = false
        )
        every { passwordEncoder.matches(any(), any()) } answers {
            firstArg<String>() == secondArg<String>()
        }

        val exception = assertThrows(BadRequestException::class.java) {
            userService.authUser(request)
        }

        exception.message shouldBe "User registration was not confirmed"
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
            isRegistered = true
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

    @Test
    fun `registerAdmin выбрасывает исключение при существующем пользователе`() {
        val request = RegisterAdminRequest(
            login = "admin",
            password = "password",
            name = "Admin",
            secret = "secret"
        )

        every { userRepository.findByLogin(request.login) } returns User(
            login = "admin",
            password = "existing",
            name = "Existing",
            isAdmin = false,
            isRegistered = true
        )


        val exception = assertThrows(UserIsAlreadyExistsException::class.java) {
            userService.registerAdmin(request)
        }

        exception.message shouldBe "User is already exists"
        verify(exactly = 1) { userRepository.findByLogin(request.login) }
    }

    @Test
    fun `registerAdmin выбрасывает исключение при неверном секрете`(environmentVariables: EnvironmentVariables) {
        val request = RegisterAdminRequest(
            login = "admin",
            password = "password",
            name = "Admin",
            secret = "invalid_secret"
        )

        environmentVariables.set("SECRET", "correct_secret")
        every { userRepository.findByLogin(request.login) } returns null

        val exception = assertThrows(BadRequestException::class.java) {
            userService.registerAdmin(request)
        }

        exception.message shouldBe "Invalid secret"

        verify(exactly = 1) { userRepository.findByLogin(request.login) }
        verify(exactly = 0) { userRepository.save(any()) }
        verify(exactly = 0) { passwordEncoder.encode(any()) }
    }

    @Test
    fun `registerAdmin сохраняет нового администратора при валидных данных`(environmentVariables: EnvironmentVariables) {
        val request = RegisterAdminRequest(
            login = "admin",
            password = "password",
            name = "Admin",
            secret = "fake"
        )

        environmentVariables.set("SECRET", "correct_secret")
        every { userRepository.findByLogin(request.login) } returns null
        every { passwordEncoder.encode(request.password) } returns "encoded_password"
        every { userRepository.save(any()) } answers { firstArg() }

        val result = userService.registerAdmin(request)

        result shouldBe StatusResponse("ok")
        verify(exactly = 1) {
            userRepository.save(match { user -> user.login == request.login && user.password == "encoded_password" && user.name == request.name && user.isAdmin == true && user.isRegistered == true
            })
        }
    }
}