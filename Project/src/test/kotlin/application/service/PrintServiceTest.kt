package application.service

import application.client.PrintClient
import application.config.Properties
import application.entity.File
import application.entity.User
import application.exception.BadRequestException
import application.exception.UnauthorizedException
import application.exception.UserNotFoundException
import application.exception.UserIsAlreadyExistsException
import application.repository.FileRepository
import application.repository.UserRepository
import application.request.AuthoriseRequest
import application.request.RegisterAdminRequest
import application.response.StatusResponse
import io.kotest.assertions.asClue
import io.kotest.assertions.throwables.shouldThrow
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
class PrintServiceTest {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var fileRepository: FileRepository

    @Autowired
    lateinit var jwtService: JwtService

    @Autowired
    lateinit var printClient: PrintClient

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    lateinit var printService: PrintService

    private val testLogin = "test@example.com"
    private val testPassword = "securePassword1488"
    private val testName = "Ryan Gosling"
    private val printedFile = "file1"
    private val notPrintedFile = "file2"
    lateinit var token: String

    @BeforeEach
    fun setup() {
        printService = PrintService(userRepository, fileRepository, printClient, jwtService)

        val user = userRepository.save(
            User(
                login = testLogin,
                password = passwordEncoder.encode(testPassword),
                name = testName,
                isConfirmed = true,
                uuid = UUID.randomUUID(),
                isAdmin = false
            )
        )

        fileRepository.save(
            File(
                user = user,
                uuid = UUID.randomUUID(),
                filePath = printedFile,
                isPrinted = true
            )
        )

        fileRepository.save(
            File(
                user = user,
                uuid = UUID.randomUUID(),
                filePath = notPrintedFile,
                isPrinted = false
            )
        )

        token = jwtService.generateToken(
            mapOf(
                "login" to user.login,
                "name" to user.name,
                "isAdmin" to false
            ),
            user.login
        )
    }

    @AfterEach
    fun cleanup() {
        userRepository.deleteAll()
    }

    @Test
    fun `получение распечатанных файлов, некорректный токен`() {
        val invalidToken = "token"

        shouldThrow<UnauthorizedException> {
            printService.getPrinted(invalidToken)
        }.message shouldBe "Authorization error"
    }

    @Test
    fun `получение распечатанных файлов, пользователя нет`() {
        val invalidToken = jwtService.generateToken(
            mapOf(
                "login" to "pupupu",
                "name" to "pupupu",
                "isAdmin" to false
            ),
            "pupupu"
        )

        shouldThrow<UserNotFoundException> {
            printService.getPrinted(invalidToken)
        }.message shouldBe "token is invalid"
    }

    @Test
    fun `получение распечатанных файлов, корректная`() {
        val result = printService.getPrinted(token)

        result.files.size shouldBe 1
        result.files[0] shouldBe printedFile
    }

    @Test
    fun `получение нераспечатанных файлов, некорректный токен`() {
        val invalidToken = "token"

        shouldThrow<UnauthorizedException> {
            printService.getNotPrinted(invalidToken)
        }.message shouldBe "Authorization error"
    }

    @Test
    fun `получение нераспечатанных файлов, пользователя нет`() {
        val invalidToken = jwtService.generateToken(
            mapOf(
                "login" to "pupupu",
                "name" to "pupupu",
                "isAdmin" to false
            ),
            "pupupu"
        )

        shouldThrow<UserNotFoundException> {
            printService.getNotPrinted(invalidToken)
        }.message shouldBe "token is invalid"
    }

    @Test
    fun `получение нераспечатанных файлов, корректная`() {
        val result = printService.getNotPrinted(token)

        result.files.size shouldBe 1
        result.files[0] shouldBe notPrintedFile
    }
}