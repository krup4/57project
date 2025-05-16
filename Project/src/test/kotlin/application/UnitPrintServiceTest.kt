package application

import application.client.PrintClient
import application.entity.File
import application.entity.User
import application.exception.UnauthorizedException
import application.exception.UserNotFoundException
import application.repository.FileRepository
import application.repository.UserRepository
import application.request.PrintFileRequest
import application.service.JwtService
import application.service.PrintService
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.web.multipart.MultipartFile
import java.util.*

class UnitPrintServiceTest {

    private val userRepository = mockk<UserRepository>()
    private val fileRepository = mockk<FileRepository>()
    private val jwtService = mockk<JwtService>()
    private val printClient = mockk<PrintClient>()
    private val testMultipartFile = mockk<MultipartFile>()

    private val printService = PrintService(userRepository, fileRepository, printClient, jwtService)

    @Test
    fun `получение распечатанных файлов, некорректный токен`() {
        val token = "token"

        every { jwtService.validateToken(token) } returns false

        val exception = assertThrows(UnauthorizedException::class.java) {
            printService.getPrinted(token)
        }

        exception.message shouldBe "Authorization error"
    }

    @Test
    fun `получение распечатанных файлов, нет пользователя`() {
        val token = "token"
        val testUser = User(
            uuid = UUID.randomUUID(),
            login = "test",
            password = "test"
        )

        every { jwtService.validateToken(token) } returns true
        every { jwtService.getLoginFromToken(token) } returns testUser.login
        every { userRepository.findByLogin(testUser.login) } returns null

        val exception = assertThrows(UserNotFoundException::class.java) {
            printService.getPrinted(token)
        }

        exception.message shouldBe "token is invalid"
    }

    @Test
    fun `получение распечатанных файлов, корректная`() {
        val token = "token"
        val testUser = User(
            uuid = UUID.randomUUID(),
            login = "test",
            password = "test"
        )
        val testFile = File(
            user = testUser,
            uuid = UUID.randomUUID(),
            filePath = "testPath"
        )

        every { jwtService.validateToken(token) } returns true
        every { jwtService.getLoginFromToken(token) } returns testUser.login
        every { userRepository.findByLogin(testUser.login) } returns testUser
        every { fileRepository.findByUserAndIsPrinted(testUser, true) } returns listOf(testFile)

        val result = printService.getPrinted(token)
        result.files.size shouldBe 1
        result.files[0] shouldBe testFile.filePath
    }

    @Test
    fun `получение нераспечатанных файлов, некорректный токен`() {
        val token = "token"

        every { jwtService.validateToken(token) } returns false

        val exception = assertThrows(UnauthorizedException::class.java) {
            printService.getNotPrinted(token)
        }

        exception.message shouldBe "Authorization error"
    }

    @Test
    fun `получение нераспечатанных файлов, нет пользователя`() {
        val token = "token"
        val testUser = User(
            uuid = UUID.randomUUID(),
            login = "test",
            password = "test"
        )

        every { jwtService.validateToken(token) } returns true
        every { jwtService.getLoginFromToken(token) } returns testUser.login
        every { userRepository.findByLogin(testUser.login) } returns null

        val exception = assertThrows(UserNotFoundException::class.java) {
            printService.getNotPrinted(token)
        }

        exception.message shouldBe "token is invalid"
    }

    @Test
    fun `получение нераспечатанных файлов, корректная`() {
        val token = "token"
        val testUser = User(
            uuid = UUID.randomUUID(),
            login = "test",
            password = "test"
        )
        val testFile = File(
            user = testUser,
            uuid = UUID.randomUUID(),
            filePath = "testPath"
        )

        every { jwtService.validateToken(token) } returns true
        every { jwtService.getLoginFromToken(token) } returns testUser.login
        every { userRepository.findByLogin(testUser.login) } returns testUser
        every { fileRepository.findByUserAndIsPrinted(testUser, false) } returns listOf(testFile)

        val result = printService.getNotPrinted(token)
        result.files.size shouldBe 1
        result.files[0] shouldBe testFile.filePath
    }

}