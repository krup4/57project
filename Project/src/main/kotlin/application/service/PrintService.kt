package application.service

import application.client.PrintClient
import application.entity.File
import application.exception.IncorrectFileException
import application.exception.UnauthorizedException
import application.repository.FileRepository
import application.repository.UserRepository
import application.request.PrintFileRequest
import application.response.FilesResponse
import application.response.StatusResponse
import jakarta.transaction.Transactional
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Service
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

@Service
class PrintService (
    private val userRepository: UserRepository,
    private val fileRepository: FileRepository,
    private val printClient: PrintClient
) {
    private val baseDirectory = Paths.get("Project/files").toAbsolutePath().normalize()

    init {
        Files.createDirectories(baseDirectory)
    }

    @Transactional
    fun print(printFileRequest: PrintFileRequest, token: String): StatusResponse {
        val user = userRepository.findByToken(token)

        if (user == null) {
            throw UnauthorizedException("Authorization error")
        }

        val fileName = printFileRequest.file.originalFilename ?: "unnamed"
        val saveDir = baseDirectory.resolve(user.login)

        if (!Files.exists(saveDir)) {
            Files.createDirectories(saveDir)
        }

        val filePath = saveDir.resolve(fileName).normalize()
        if (!filePath.startsWith(baseDirectory)) {
            throw IncorrectFileException("Incorrect name of the file")
        }

        Files.copy(printFileRequest.file.inputStream, filePath, StandardCopyOption.REPLACE_EXISTING)

        var isPrinted: Boolean

        val printResponse = printClient.printFile(printFileRequest.file)

        if (printResponse.statusCode == HttpStatusCode.valueOf(200)) {
            isPrinted = true
        } else {
            isPrinted = false
        }

        val file = fileRepository.findByUserAndFilePath(user, filePath.toAbsolutePath().toString())

        if (file != null) {
            file.isPrinted = isPrinted
            fileRepository.save(file)
            return StatusResponse("ok")
        }

        fileRepository.save(
            File(
                user = user,
                filePath = filePath.toAbsolutePath().toString(),
                isPrinted = isPrinted
            )
        )

        return StatusResponse(printResponse.body.message)
    }

    fun getNotPrinted(token: String): FilesResponse {
        val user = userRepository.findByToken(token)

        if (user == null) {
            throw UnauthorizedException("Authorization error")
        }

        val files = fileRepository.findByUserAndIsPrinted(user, false).map { it.filePath }

        return FilesResponse(files)
    }

    fun getPrinted(token: String): FilesResponse {
        val user = userRepository.findByToken(token)

        if (user == null) {
            throw UnauthorizedException("Authorization error")
        }

        val files = fileRepository.findByUserAndIsPrinted(user, true).map { it.filePath }

        return FilesResponse(files)
    }
}