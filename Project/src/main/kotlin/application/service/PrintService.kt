package application.service

import application.exception.IncorrectFileException
import application.exception.UnauthorizedException
import application.repository.FileRepository
import application.repository.UserRepository
import application.request.PrintFileRequest
import application.response.FilesResponse
import application.response.StatusResponse
import org.springframework.stereotype.Service
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

@Service
class PrintService (
    private val userRepository: UserRepository,
    private val fileRepository: FileRepository,
) {
    private val saveDir = Paths.get("Project/files").toAbsolutePath().normalize()

    init {
        Files.createDirectories(saveDir)
    }

    fun print(printFileRequest: PrintFileRequest, token: String): StatusResponse {
        val user = userRepository.findByToken(token)

        if (user == null) {
            throw UnauthorizedException("Authorization error")
        }

        val fileName = printFileRequest.file.originalFilename ?: "unnamed"
        val filePath = saveDir.resolve(fileName).normalize()
        if (!filePath.startsWith(saveDir)) {
            throw IncorrectFileException("Incorrect name of the file")
        }

        Files.copy(printFileRequest.file.inputStream, filePath, StandardCopyOption.REPLACE_EXISTING)

        return StatusResponse("ok")
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