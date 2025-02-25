package application.service

import application.request.PrintFile
import application.response.FilesResponse
import application.response.StatusResponse
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class PrintService {

    fun print(printFIle: PrintFile, token: String): ResponseEntity<StatusResponse> {
        TODO()
    }

    fun getNotPrinted(token: String): ResponseEntity<FilesResponse> {
        TODO()
    }

    fun getPrinted(token: String): ResponseEntity<FilesResponse> {
        TODO()
    }
}