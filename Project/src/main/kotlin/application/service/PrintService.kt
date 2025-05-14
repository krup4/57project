package application.service

import application.client.PrintClient
import application.entity.File
import application.exception.ClientErrorException
import application.exception.IncorrectFileException
import application.exception.UnauthorizedException
import application.exception.UserNotFoundException
import application.repository.FileRepository
import application.repository.UserRepository
import application.request.PrintFileRequest
import application.response.FilesResponse
import application.response.StatusResponse
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.UUID

@Service
class PrintService (
    private val userRepository: UserRepository,
    private val fileRepository: FileRepository,
    private val printClient: PrintClient,
    private val metricRegister: MeterRegistry,
    private val jwtService: JwtService
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val baseDirectory = Paths.get("Project/files").toAbsolutePath().normalize()
    private val counter = metricRegister.counter("PrintFileCounter")

    init {
        Files.createDirectories(baseDirectory)
    }

    @Transactional
    fun print(printFileRequest: PrintFileRequest, token: String): StatusResponse {
        if (!jwtService.validateToken(token)) {
            throw UserNotFoundException("token is invalid")
        }

        val login = jwtService.getLoginFromToken(token)
        val user = userRepository.findByLogin(login)


        if (user == null) {
            throw UnauthorizedException("Authorization error")
        }

        counter.increment()

        val fileName = printFileRequest.file.originalFilename ?: "unnamed"
        val saveDir = baseDirectory.resolve(user.login)

        if (!Files.exists(saveDir)) {
            Files.createDirectories(saveDir)
        }

        val filePath = saveDir.resolve(fileName).normalize()
        if (!filePath.startsWith(baseDirectory)) {
            throw IncorrectFileException("Incorrect name of the file")
        }

        var curFileId: UUID

        val file = fileRepository.findByUserAndFilePath(user, filePath.toAbsolutePath().toString())

        if (file != null) {
            curFileId = file.uuid
        } else {
            curFileId = UUID.randomUUID()
        }

        Files.copy(printFileRequest.file.inputStream, filePath, StandardCopyOption.REPLACE_EXISTING)
        logger.info("Файл сохранен : ${fileName}, id: ${curFileId}")


        var isPrinted: Boolean

        var printResponse: ResponseEntity<StatusResponse>
        try {
            printResponse = printClient.printFile(printFileRequest.file)
        } catch (e: Exception) {
            throw ClientErrorException("Error in client work")
        }

        if (printResponse.statusCode == HttpStatusCode.valueOf(200)) {
            logger.info("Файл ${curFileId} распечатан")
            isPrinted = true
        } else {
            logger.warn("Файл ${curFileId} не распечатан")
            isPrinted = false
        }

        if (file != null) {
            file.isPrinted = isPrinted
            fileRepository.save(file)
            return StatusResponse("ok")
        }

        fileRepository.save(
            File(
                user = user,
                filePath = filePath.toAbsolutePath().toString(),
                isPrinted = isPrinted,
                uuid = curFileId
            )
        )
        logger.info("Сохранен файл ${curFileId}; user : ${user}, filePath : ${filePath.toAbsolutePath().toString()}, isPrinted : ${isPrinted}")
        return StatusResponse(printResponse.body.message)
    }

    fun getNotPrinted(token: String): FilesResponse {
        if (!jwtService.validateToken(token)) {
            throw UserNotFoundException("token is invalid")
        }

        val login = jwtService.getLoginFromToken(token)
        val user = userRepository.findByLogin(login)


        if (user == null) {
            throw UnauthorizedException("Authorization error")
        }

        val files = fileRepository.findByUserAndIsPrinted(user, false).map { it.filePath }
        logger.info("Возвращен список нераспечатанных файлов")
        return FilesResponse(files)
    }

    fun getPrinted(token: String): FilesResponse {
        if (!jwtService.validateToken(token)) {
            throw UserNotFoundException("token is invalid")
        }

        val login = jwtService.getLoginFromToken(token)
        val user = userRepository.findByLogin(login)


        if (user == null) {
            throw UnauthorizedException("Authorization error")
        }

        val files = fileRepository.findByUserAndIsPrinted(user, true).map { it.filePath }
        logger.info("Возвращен список распечатанных файлов")
        return FilesResponse(files)
    }
}