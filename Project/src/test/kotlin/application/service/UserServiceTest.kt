package application.service

import application.config.Properties
import application.entity.User
import application.exception.BadRequestException
import application.exception.UserIsAlreadyExistsException
import application.repository.UserRepository
import application.request.RegisterAdminRequest
import application.response.StatusResponse
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder

@SpringBootTest(
    properties = ["spring.profiles.active=test"]
)
class UserServiceTest (
    @Autowired
    val userRepository: UserRepository,

    @Autowired
    val passwordEncoder: PasswordEncoder,

    @Autowired
    val jwtService: JwtService,

    @Autowired
    val properties: Properties
) {
    val userService = UserService(userRepository, passwordEncoder, jwtService, properties)

    @Test
    fun `registerAdmin должен корректно сохрянять админа при вводе валидных данных`() {
        userRepository.deleteAll()

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
        userRepository.deleteAll()

        val existingLogin = "existing_admin"
        userRepository.save(
            User(
                login = existingLogin,
                password = "hash",
                name = "Existing",
                isAdmin = false,
                isRegistered = true
            )
        )

        val request = RegisterAdminRequest(
            login = existingLogin,
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
        userRepository.deleteAll()

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