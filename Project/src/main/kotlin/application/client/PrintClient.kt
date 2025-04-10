package application.client

import application.request.PrintFileRequest
import application.response.StatusResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping

@FeignClient(name = "printClient", url = "http://localhost:8080/")
interface PrintClient {

    @PostMapping("/print")
    fun printFile(printFileRequest: PrintFileRequest): ResponseEntity<StatusResponse>
}