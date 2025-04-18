package application.service

import application.config.Properties
import application.entity.User
import application.exception.BadRequestException
import application.exception.UserNotFoundException
import application.exception.UserIsAlreadyExistsException
import application.repository.UserRepository
import application.request.AuthoriseRequest
import application.request.RegisterAdminRequest
import application.response.StatusResponse
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.annotation.Transactional
import org.junit.jupiter.api.assertThrows

@SpringBootTest(
    properties = [
        "spring.profiles.active=test"
    ]
)
@Transactional
class UserServiceTest {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    lateinit var jwtService: JwtService

    @Autowired
    lateinit var properties: Properties

    lateinit var userService: UserService

    private val testLogin = "test@example.com"
    private val testPassword = "securePassword1488"
    private val testName = "Ryan Gosling"

    @BeforeEach
    fun setup() {
        userService = UserService(userRepository, passwordEncoder, jwtService, properties)

        userRepository.save(
            User(
                login = testLogin,
                password = passwordEncoder.encode(testPassword),
                name = testName,
                isRegistered = true
            )
        )
    }

    @AfterEach
    fun cleanup() {
        userRepository.deleteAll()
    }

    @Test
    fun `authUser должен возвращать валидный токен при корректных учетных данных`() {
        val request = AuthoriseRequest(testLogin, testPassword)
        val response = userService.authUser(request)

        response.token shouldNotBe null
        response.token.isBlank() shouldBe false

        val user = userRepository.findByLogin(testLogin)
        user?.token shouldBe response.token
    }

    @Test
    fun `authUser должен выбрасывать UserNotFoundException при неверном логине`() {
        val request = AuthoriseRequest("invalid@example.com", testPassword)

        val exception = assertThrows<UserNotFoundException> {
            userService.authUser(request)
        }
        exception.message shouldBe "User not found"
    }

    @Test
    fun `authUser должен выбрасывать BadRequestException при неверном пароле`() {
        val request = AuthoriseRequest(testLogin, "wrong_password")

        val exception = assertThrows<BadRequestException> {
            userService.authUser(request)
        }
        exception.message shouldBe "Password is incorrect"
    }

    @Test
    fun `authUser должен выбрасывать BadRequestException для незарегистрированного пользователя`() {
        userRepository.save(
            User(
                login = "unregistered@test.com",
                password = passwordEncoder.encode(testPassword),
                isRegistered = false
            )
        )
        val request = AuthoriseRequest("unregistered@test.com", testPassword)


        val exception = assertThrows<BadRequestException> {
            userService.authUser(request)
        }
        exception.message shouldBe "User registration was not confirmed"
    }

    @Test
    fun `registerAdmin должен корректно сохрянять админа при вводе валидных данных`() {
        val request = RegisterAdminRequest(
            login = "admin",
            password = "password",
            name = "Admin",
            secret = properties.secret
        )

        val result = userService.registerAdmin(request)

        result shouldBe StatusResponse("ok")

        val savedUser = userRepository.findByLogin(request.login)
        savedUser shouldNotBe null
        savedUser!!.apply {
            login shouldBe request.login
            name shouldBe request.name
            isAdmin shouldBe true
            isRegistered shouldBe true
            passwordEncoder.matches(request.password, password) shouldBe true
        }
    }

    @Test
    fun `registerAdmin должен выбрасывать UserIsAlreadyExistsException, когда пользователь уже существует`() {
        val request = RegisterAdminRequest(
            login = testLogin,
            password = "password",
            name = "New Admin",
            secret = properties.secret
        )

        shouldThrow<UserIsAlreadyExistsException> {
            userService.registerAdmin(request)
        }.message shouldBe "User is already exists"
    }

    @Test
    fun `registerAdmin должен выбрасывать BadRequestException, когда введен неправильный секрет`() {
        val request = RegisterAdminRequest(
            login = "admin",
            password = "password",
            name = "Admin",
            secret = "invalid_secret"
        )

        shouldThrow<BadRequestException> {
            userService.registerAdmin(request)
        }.message shouldBe "Invalid secret"
    }

}