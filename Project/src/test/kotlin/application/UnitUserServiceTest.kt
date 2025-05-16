package application

import application.config.Properties
import application.entity.User
import application.exception.BadRequestException
import application.exception.UserIsAlreadyExistsException
import application.exception.UserNotFoundException
import application.exception.UserIsNotAdminException
import application.exception.UnauthorizedException
import application.repository.UserRepository
import application.request.AuthoriseRequest
import application.request.AcceptUserRequest
import application.request.SignUpRequest
import application.request.RegisterAdminRequest
import application.service.JwtService
import application.service.UserService
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Assertions.assertThrows
import org.springframework.security.crypto.password.PasswordEncoder
import application.response.StatusResponse
import io.kotest.assertions.asClue
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.*
import org.apache.commons.collections4.functors.TruePredicate
import org.junit.jupiter.api.*
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables
import java.util.*
import io.kotest.matchers.shouldNotBe

class UnitUserServiceTest {

    private val userRepository = mockk<UserRepository>()
    private val passwordEncoder = mockk<PasswordEncoder>()
    private val jwtService = mockk<JwtService>()
    private val properties = Properties()
    private val userService = UserService(userRepository, passwordEncoder, jwtService, properties)


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
            uuid = UUID.randomUUID()
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
            isConfirmed = false,
            uuid = UUID.randomUUID()
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
            isConfirmed = true,
            uuid = UUID.randomUUID()
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
            isConfirmed = true,
            uuid = UUID.randomUUID()
        )


        val exception = assertThrows(UserIsAlreadyExistsException::class.java) {
            userService.registerAdmin(request)
        }

        exception.message shouldBe "User is already exists"
        verify(exactly = 1) { userRepository.findByLogin(request.login) }
    }

    @Test
    fun `registerAdmin выбрасывает исключение при неверном секрете`() {
        val request = RegisterAdminRequest(
            login = "admin",
            password = "password",
            name = "Admin",
            secret = "invalid_secret"
        )

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
    fun `registerAdmin сохраняет нового администратора при валидных данных`() {
        val request = RegisterAdminRequest(
            login = "admin",
            password = "password",
            name = "Admin",
            secret = "test"
        )

        every { userRepository.findByLogin(request.login) } returns null
        every { passwordEncoder.encode(request.password) } returns "encoded_password"
        every { userRepository.save(any()) } answers { firstArg() }

        val result = userService.registerAdmin(request)

        result.shouldBeInstanceOf<User>()
        result.asClue { user: User ->
            user.login shouldBe request.login
            user.name shouldBe request.name
            user.isAdmin shouldBe true
            user.isConfirmed shouldBe true
        }
        verify(exactly = 1) {
            userRepository.save(match { user ->
                user.login == request.login && user.password == "encoded_password" && user.name == request.name && user.isAdmin == true && user.isConfirmed == true
            })
        }
    }

    @Test
    fun `registerUser выбрасывает исключение при существующем пользователе`() {
        val request = SignUpRequest(
            login = "existing@example.com",
            password = "password",
            name = "Existing User"
        )

        every { userRepository.findByLogin(request.login) } returns User(
            login = request.login,
            password = "oldPassword",
            name = request.name,
            isAdmin = false,
            isConfirmed = false,
            uuid = UUID.randomUUID()
        )

        val exception = assertThrows(UserIsAlreadyExistsException::class.java) {
            userService.registerUser(request)
        }

        exception.message shouldBe "User is already exists"
        verify(exactly = 1) { userRepository.findByLogin(request.login) }
        verify(exactly = 0) { passwordEncoder.encode(any()) }
        verify(exactly = 0) { userRepository.save(any()) }
    }

    @Test
    fun `registerUser сохраняет нового пользователя с корректными данными`() {
        val request = SignUpRequest(
            login = "new@example.com",
            password = "password",
            name = "New User"
        )

        val encodedPassword = "encoded_password"
        val expectedUuid = UUID.randomUUID()

        every { userRepository.findByLogin(request.login) } returns null
        every { passwordEncoder.encode(request.password) } returns encodedPassword
        every { userRepository.save(any()) } answers {
            val user = firstArg<User>()
            every { user.uuid } returns expectedUuid
            user
        }

        val result = userService.registerUser(request)

        result.asClue { user ->
            user.login shouldBe request.login
            user.name shouldBe request.name
            user.password shouldBe encodedPassword
            user.isAdmin shouldBe false
            user.isConfirmed shouldBe false
            user.uuid shouldBe expectedUuid
        }

        verify(exactly = 1) {
            userRepository.save(match { user ->
                user.login == request.login &&
                        user.password == encodedPassword &&
                        user.name == request.name &&
                        user.isAdmin == false &&
                        user.isConfirmed == false
            })
        }
        verify(exactly = 1) { passwordEncoder.encode(request.password) }
    }

    @Test
    fun `registerUser вызывает passwordEncoder с правильным паролем`() {
        val request = SignUpRequest(
            login = "test@example.com",
            password = "testPassword123",
            name = "Test User"
        )

        every { userRepository.findByLogin(request.login) } returns null
        every { passwordEncoder.encode(request.password) } returns "encoded_password"
        every { userRepository.save(any()) } returns mockk()

        userService.registerUser(request)

        verify(exactly = 1) { passwordEncoder.encode("testPassword123") }
    }

    @Test
    fun `registerUser устанавливает правильные значения по умолчанию`() {
        val request = SignUpRequest(
            login = "default@example.com",
            password = "password",
            name = "Default User"
        )

        every { userRepository.findByLogin(request.login) } returns null
        every { passwordEncoder.encode(any()) } returns "encoded_password"
        every { userRepository.save(any()) } answers { firstArg() }

        val result = userService.registerUser(request)

        result.asClue { user ->
            user.isAdmin shouldBe false
            user.isConfirmed shouldBe false
            user.uuid shouldNotBe null
        }
    }

    @Test
    fun `getNotRegistered выбрасывает исключение при невалидном токене`() {
        val invalidToken = "invalid_token"

        every { jwtService.validateToken(invalidToken) } returns false

        val exception = assertThrows(UserNotFoundException::class.java) {
            userService.getNotRegistered(invalidToken)
        }

        exception.message shouldBe "token is invalid"
        verify(exactly = 1) { jwtService.validateToken(invalidToken) }
        verify(exactly = 0) { jwtService.getLoginFromToken(any()) }
        verify(exactly = 0) { userRepository.findByLogin(any()) }
    }

    @Test
    fun `getNotRegistered выбрасывает исключение при отсутствии пользователя`() {
        val validToken = "valid_token"
        val login = "admin@example.com"

        every { jwtService.validateToken(validToken) } returns true
        every { jwtService.getLoginFromToken(validToken) } returns login
        every { userRepository.findByLogin(login) } returns null

        val exception = assertThrows(UnauthorizedException::class.java) {
            userService.getNotRegistered(validToken)
        }

        exception.message shouldBe "Authorization error"
        verify(exactly = 1) { jwtService.validateToken(validToken) }
        verify(exactly = 1) { jwtService.getLoginFromToken(validToken) }
        verify(exactly = 1) { userRepository.findByLogin(login) }
        verify(exactly = 0) { userRepository.findByIsConfirmed(any()) }
    }

    @Test
    fun `getNotRegistered выбрасывает исключение если пользователь не админ`() {
        val validToken = "valid_token"
        val login = "user@example.com"
        val regularUser = User(
            login = login,
            password = "password",
            name = "Regular User",
            isAdmin = false,
            isConfirmed = true,
            uuid = UUID.randomUUID()
        )

        every { jwtService.validateToken(validToken) } returns true
        every { jwtService.getLoginFromToken(validToken) } returns login
        every { userRepository.findByLogin(login) } returns regularUser

        val exception = assertThrows(UserIsNotAdminException::class.java) {
            userService.getNotRegistered(validToken)
        }

        exception.message shouldBe "User is not admin"
        verify(exactly = 1) { jwtService.validateToken(validToken) }
        verify(exactly = 1) { jwtService.getLoginFromToken(validToken) }
        verify(exactly = 1) { userRepository.findByLogin(login) }
        verify(exactly = 0) { userRepository.findByIsConfirmed(any()) }
    }

    @Test
    fun `getNotRegistered возвращает список неподтвержденных пользователей для админа`() {
        val validToken = "valid_token"
        val login = "admin@example.com"
        val adminUser = User(
            login = login,
            password = "password",
            name = "Admin User",
            isAdmin = true,
            isConfirmed = true,
            uuid = UUID.randomUUID()
        )
        val notRegisteredUsers = listOf(
            User(
                login = "user1@example.com",
                password = "pass1",
                name = "User 1",
                isAdmin = false,
                isConfirmed = false,
                uuid = UUID.randomUUID()
            ),
            User(
                login = "user2@example.com",
                password = "pass2",
                name = "User 2",
                isAdmin = false,
                isConfirmed = false,
                uuid = UUID.randomUUID()
            )
        )

        every { jwtService.validateToken(validToken) } returns true
        every { jwtService.getLoginFromToken(validToken) } returns login
        every { userRepository.findByLogin(login) } returns adminUser
        every { userRepository.findByIsConfirmed(false) } returns notRegisteredUsers

        val result = userService.getNotRegistered(validToken)

        result.users shouldBe notRegisteredUsers
        //verify(exactly = 1) { logger.info("Получен список неподтвержденных пользователей") }
        verify(exactly = 1) { jwtService.validateToken(validToken) }
        verify(exactly = 1) { jwtService.getLoginFromToken(validToken) }
        verify(exactly = 1) { userRepository.findByLogin(login) }
        verify(exactly = 1) { userRepository.findByIsConfirmed(false) }
    }

    @Test
    fun `getNotRegistered возвращает пустой список если нет неподтвержденных пользователей`() {
        val validToken = "valid_token"
        val login = "admin@example.com"
        val adminUser = User(
            login = login,
            password = "password",
            name = "Admin User",
            isAdmin = true,
            isConfirmed = true,
            uuid = UUID.randomUUID()
        )

        every { jwtService.validateToken(validToken) } returns true
        every { jwtService.getLoginFromToken(validToken) } returns login
        every { userRepository.findByLogin(login) } returns adminUser
        every { userRepository.findByIsConfirmed(false) } returns emptyList()

        val result = userService.getNotRegistered(validToken)

        result.users shouldBe emptyList()
        //verify(exactly = 1) { logger.info("Получен список неподтвержденных пользователей") }
    }

    @Test
    fun `acceptUser выбрасывает исключение при невалидном токене`() {
        val invalidToken = "invalid_token"
        val request = AcceptUserRequest(login = "user@test.com", isConfirmed = true)

        every { jwtService.validateToken(invalidToken) } returns false

        val exception = assertThrows(UserNotFoundException::class.java) {
            userService.acceptUser(request, invalidToken)
        }

        exception.message shouldBe "token is invalid"
        verify(exactly = 1) { jwtService.validateToken(invalidToken) }
        verify(exactly = 0) { jwtService.getLoginFromToken(any()) }
        verify(exactly = 0) { userRepository.findByLogin(any()) }
    }

    @Test
    fun `acceptUser выбрасывает исключение при отсутствии авторизованного пользователя`() {
        val validToken = "valid_token"
        val login = "admin@test.com"
        val request = AcceptUserRequest(login = "user@test.com", isConfirmed = true)

        every { jwtService.validateToken(validToken) } returns true
        every { jwtService.getLoginFromToken(validToken) } returns login
        every { userRepository.findByLogin(login) } returns null

        val exception = assertThrows(UnauthorizedException::class.java) {
            userService.acceptUser(request, validToken)
        }

        exception.message shouldBe "Authorization error"
        verify(exactly = 1) { jwtService.validateToken(validToken) }
        verify(exactly = 1) { jwtService.getLoginFromToken(validToken) }
        verify(exactly = 1) { userRepository.findByLogin(login) }
        verify(exactly = 0) { userRepository.findByLogin(request.login) }
    }

    @Test
    fun `acceptUser выбрасывает исключение если пользователь не админ`() {
        val validToken = "valid_token"
        val login = "user@test.com"
        val request = AcceptUserRequest(login = "user2@test.com", isConfirmed = true)
        val regularUser = User(
            login = login,
            password = "password",
            name = "Regular User",
            isAdmin = false,
            isConfirmed = true,
            uuid = UUID.randomUUID()
        )

        every { jwtService.validateToken(validToken) } returns true
        every { jwtService.getLoginFromToken(validToken) } returns login
        every { userRepository.findByLogin(login) } returns regularUser

        val exception = assertThrows(UserIsNotAdminException::class.java) {
            userService.acceptUser(request, validToken)
        }

        exception.message shouldBe "User is not admin"
        verify(exactly = 1) { jwtService.validateToken(validToken) }
        verify(exactly = 1) { jwtService.getLoginFromToken(validToken) }
        verify(exactly = 1) { userRepository.findByLogin(login) }
        verify(exactly = 0) { userRepository.findByLogin(request.login) }
    }

    @Test
    fun `acceptUser выбрасывает исключение если подтверждаемый пользователь не найден`() {
        val validToken = "valid_token"
        val adminLogin = "admin@test.com"
        val request = AcceptUserRequest(login = "nonexistent@test.com", isConfirmed = true)
        val adminUser = User(
            login = adminLogin,
            password = "password",
            name = "Admin User",
            isAdmin = true,
            isConfirmed = true,
            uuid = UUID.randomUUID()
        )

        every { jwtService.validateToken(validToken) } returns true
        every { jwtService.getLoginFromToken(validToken) } returns adminLogin
        every { userRepository.findByLogin(adminLogin) } returns adminUser
        every { userRepository.findByLogin(request.login) } returns null

        val exception = assertThrows(UserNotFoundException::class.java) {
            userService.acceptUser(request, validToken)
        }

        exception.message shouldBe "User is not found"
        verify(exactly = 1) { jwtService.validateToken(validToken) }
        verify(exactly = 1) { jwtService.getLoginFromToken(validToken) }
        verify(exactly = 1) { userRepository.findByLogin(adminLogin) }
        verify(exactly = 1) { userRepository.findByLogin(request.login) }
    }

    @Test
    fun `acceptUser подтверждает пользователя при isConfirmed=true`() {
        val validToken = "valid_token"
        val adminLogin = "admin@test.com"
        val userLogin = "user@test.com"
        val request = AcceptUserRequest(login = userLogin, isConfirmed = true)
        val adminUser = User(
            login = adminLogin,
            password = "password",
            name = "Admin User",
            isAdmin = true,
            isConfirmed = true,
            uuid = UUID.randomUUID()
        )
        val userToAccept = User(
            login = userLogin,
            password = "password",
            name = "Regular User",
            isAdmin = false,
            isConfirmed = false,
            uuid = UUID.randomUUID()
        )

        every { jwtService.validateToken(validToken) } returns true
        every { jwtService.getLoginFromToken(validToken) } returns adminLogin
        every { userRepository.findByLogin(adminLogin) } returns adminUser
        every { userRepository.findByLogin(userLogin) } returns userToAccept
        every { userRepository.save(any()) } returnsArgument 0

        val result = userService.acceptUser(request, validToken)

        result shouldBe StatusResponse("ok")
        verify(exactly = 1) {
            userRepository.save(match { user ->
                user.login == userLogin && user.isConfirmed == true
            })
        }
        //verify(exactly = 1) { logger.info("Регистрация пользователя ${userToAccept.uuid} подтверждена") }
    }

    @Test
    fun `acceptUser удаляет пользователя при isConfirmed=false`() {
        val validToken = "valid_token"
        val adminLogin = "admin@test.com"
        val userLogin = "user@test.com"
        val request = AcceptUserRequest(login = userLogin, isConfirmed = false)
        val adminUser = User(
            login = adminLogin,
            password = "password",
            name = "Admin User",
            isAdmin = true,
            isConfirmed = true,
            uuid = UUID.randomUUID()
        )
        val userToReject = User(
            login = userLogin,
            password = "password",
            name = "Regular User",
            isAdmin = false,
            isConfirmed = false,
            uuid = UUID.randomUUID()
        )

        every { jwtService.validateToken(validToken) } returns true
        every { jwtService.getLoginFromToken(validToken) } returns adminLogin
        every { userRepository.findByLogin(adminLogin) } returns adminUser
        every { userRepository.findByLogin(userLogin) } returns userToReject
        every { userRepository.delete(userToReject) } just Runs

        val result = userService.acceptUser(request, validToken)

        result shouldBe StatusResponse("ok")
        verify(exactly = 1) { userRepository.delete(userToReject) }
        verify(exactly = 0) { userRepository.save(any()) }
        //verify(exactly = 1) { logger.info("Регистрация пользователя ${userToReject.uuid} не подтверждена") }
    }
}