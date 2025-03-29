package application.service

import application.exception.UnauthorizedException
import application.repository.FileRepository
import application.repository.UserRepository
import application.request.PrintFileRequest
import application.response.FilesResponse
import application.response.StatusResponse
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class PrintService (
    private val userRepository: UserRepository,
    private val fileRepository: FileRepository
) {

    fun print(printFileRequest: PrintFileRequest, token: String): ResponseEntity<StatusResponse> {
        TODO()
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