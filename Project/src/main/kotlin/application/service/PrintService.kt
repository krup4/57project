package application.service

import application.request.PrintFileRequest
import application.response.FilesResponse
import application.response.StatusResponse
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class PrintService {

    fun print(printFileRequest: PrintFileRequest, token: String): ResponseEntity<StatusResponse> {
        TODO()
    }

    fun getNotPrinted(token: String): ResponseEntity<FilesResponse> {
        TODO()
    }

    fun getPrinted(token: String): ResponseEntity<FilesResponse> {
        TODO()
    }
}