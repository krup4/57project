package application.service

import application.config.Properties
import application.entity.User
import application.exception.*
import application.repository.UserRepository
import application.request.AcceptUserRequest
import application.request.AuthoriseRequest
import application.request.RegisterAdminRequest
import application.request.SignUpRequest
import application.response.StatusResponse
import io.kotest.assertions.asClue
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.annotation.Transactional
import org.junit.jupiter.api.assertThrows
import java.util.*

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
    private lateinit var adminUser: User
    private lateinit var regularUser: User
    private lateinit var unconfirmedUser: User

    private lateinit var adminToken: String
    private lateinit var regularUserToken: String

    @BeforeEach
    fun setup() {
        userService = UserService(userRepository, passwordEncoder, jwtService, properties)

        userRepository.save(
            User(
                login = testLogin,
                password = passwordEncoder.encode(testPassword),
                name = testName,
                isConfirmed = true,
                uuid = UUID.randomUUID()
            )
        )

        adminUser = userRepository.save(
            User(
                login = "admin@test.com",
                password = "admin_pass",
                uuid = UUID.randomUUID(),
                name = "Admin",
                isAdmin = true,
                isConfirmed = true
            )
        )

        adminToken = jwtService.generateToken(
            mapOf(
                "login" to adminUser.login,
                "name" to adminUser.name,
                "isAdmin" to true
            ),
            adminUser.login
        )


        regularUser = userRepository.save(
            User(
                login = "user@test.com",
                password = "user_pass",
                uuid = UUID.randomUUID(),
                name = "Regular User",
                isAdmin = false,
                isConfirmed = true
            )
        )
        regularUserToken = jwtService.generateToken(
            mapOf(
                "login" to regularUser.login,
                "name" to regularUser.name,
                "isAdmin" to false
            ),
            regularUser.login
        )

        unconfirmedUser = userRepository.save(
            User(
                login = "unconfirmed@test.com",
                password = "password",
                uuid = UUID.randomUUID(),
                isConfirmed = false
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
                isConfirmed = false,
                uuid = UUID.randomUUID()
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

        result.shouldBeInstanceOf<User>()
        result.asClue { user: User ->
            user.login shouldBe request.login
            user.name shouldBe request.name
            user.isAdmin shouldBe true
            user.isConfirmed shouldBe true
        }

        val savedUser = userRepository.findByLogin(request.login)
        savedUser shouldNotBe null
        savedUser!!.apply {
            login shouldBe request.login
            name shouldBe request.name
            isAdmin shouldBe true
            isConfirmed shouldBe true
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

    @Test
    fun `registrUser должен корректо сохранять нового пользователя ` () {
        val request = SignUpRequest(
            login = "user",
            password = "password",
            name = "User"
        )

        val result = userService.registerUser(request)

        result.shouldBeInstanceOf<User>()
        result.asClue { user: User ->
            user.login shouldBe request.login
            user.name shouldBe request.name
            user.isAdmin shouldBe false
            user.isConfirmed shouldBe false
        }

        val savedUser = userRepository.findByLogin(request.login)

        savedUser shouldNotBe null
        savedUser!!.apply {
            login shouldBe request.login
            name shouldBe request.name
            passwordEncoder.matches(request.password, password) shouldBe true
            isAdmin shouldBe false
            isConfirmed shouldBe false
            passwordEncoder.matches(request.password, password) shouldBe true
        }
    }

    @Test
    fun `registrUser должен выбрасывать User is already exists, если пользователь уже существует` () {
        val request = RegisterAdminRequest(
            login = testLogin,
            password = "password",
            name = "New User",
            secret = properties.secret
        )

        shouldThrow<UserIsAlreadyExistsException> {
            userService.registerAdmin(request)
        }.message shouldBe "User is already exists"
    }

    @Test
    fun `getNotRegistered должен выбрасывать исключение при невалидном токене`() {
        shouldThrow<UserNotFoundException> {
            userService.getNotRegistered("invalid_token_123")
        }.message shouldBe "token is invalid"
    }

    @Test
    fun `getNotRegistered должен проверять права администратора`() {
        shouldThrow<UserIsNotAdminException> {
            userService.getNotRegistered(regularUserToken)
        }.message shouldBe "User is not admin"
    }

    @Test
    fun `acceptUser должен подтверждать пользователя при isConfirmed = true`() {
        val request = AcceptUserRequest(
            login = unconfirmedUser.login,
            isConfirmed = true
        )

        val response = userService.acceptUser(request, adminToken)

        response shouldBe StatusResponse("ok")
        userRepository.findByLogin(unconfirmedUser.login)!!.isConfirmed shouldBe true
    }

    @Test
    fun `acceptUser должен удалять пользователя при isConfirmed = false`() {
        val request = AcceptUserRequest(
            login = unconfirmedUser.login,
            isConfirmed = false
        )

        val response = userService.acceptUser(request, adminToken)

        response shouldBe StatusResponse("ok")
        userRepository.findByLogin(unconfirmedUser.login) shouldBe null
    }

    @Test
    fun `acceptUser должен выбрасывать исключение при невалидном токене админа`() {
        val request = AcceptUserRequest(unconfirmedUser.login, true)

        shouldThrow<UserNotFoundException> {
            userService.acceptUser(request, "invalid_token")
        }.message shouldBe "token is invalid"
    }

    @Test
    fun `acceptUser должен проверять права администратора`() {
        val request = AcceptUserRequest(unconfirmedUser.login, true)

        shouldThrow<UserIsNotAdminException> {
            userService.acceptUser(request, regularUserToken)
        }.message shouldBe "User is not admin"
    }

    @Test
    fun `acceptUser должен выбрасывать исключение если пользователь не найден`() {
        val request = AcceptUserRequest("non_existing@test.com", true)

        shouldThrow<UserNotFoundException> {
            userService.acceptUser(request, adminToken)
        }.message shouldBe "User is not found"
    }

    @Test
    fun `acceptUser должен корректно обрабатывать уже подтвержденного пользователя`() {
        val confirmedUser = userRepository.save(
            User(
                login = "already_confirmed@test.com",
                password = "password",
                uuid = UUID.randomUUID(),
                isConfirmed = true
            )
        )

        val request = AcceptUserRequest(confirmedUser.login, true)

        val response = userService.acceptUser(request, adminToken)

        userRepository.findByLogin(confirmedUser.login)!!.apply {
            isConfirmed shouldBe true
        }
        response shouldBe StatusResponse("ok")
    }

    @Test
    fun `getNotRegistered должен выбрасывать UnauthorizedException при отсутствии пользователя в базе`() {
        val fakeToken = jwtService.generateToken(
            mapOf("login" to "ghost@test.com", "name" to "Ghost", "isAdmin" to true),
            "ghost@test.com"
        )

        shouldThrow<UnauthorizedException> {
            userService.getNotRegistered(fakeToken)
        }.message shouldBe "Authorization error"
    }

    @Test
    fun `getNotRegistered должен возвращать пустой список если нет неподтвержденных пользователей`() {
        userRepository.delete(unconfirmedUser)

        val response = userService.getNotRegistered(adminToken)
        response.users shouldHaveSize 0
    }

    @Test
    fun `acceptUser должен выбрасывать UnauthorizedException если пользователь с токеном не существует`() {
        val fakeToken = jwtService.generateToken(
            mapOf("login" to "fake_admin@test.com", "name" to "Fake Admin", "isAdmin" to true),
            "fake_admin@test.com"
        )

        val request = AcceptUserRequest("any_user@test.com", true)

        shouldThrow<UnauthorizedException> {
            userService.acceptUser(request, fakeToken)
        }.message shouldBe "Authorization error"
    }

    @Test
    fun `acceptUser должен откатывать транзакцию при ошибке`() {
        val initialCount = userRepository.count()

        try {
            userService.acceptUser(AcceptUserRequest("invalid@user.com", true), "invalid_token")
        } catch (e: Exception) {

        }

        userRepository.count() shouldBe initialCount
    }

}