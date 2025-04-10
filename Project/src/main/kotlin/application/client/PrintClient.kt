package application.client

import application.request.PrintFileRequest
import application.response.StatusResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping

@FeignClient(name = "printClient", url = "http://46.138.45.1:8080/")
interface PrintClient {

    @PostMapping("/print")
    fun printFile(printFileRequest: PrintFileRequest): ResponseEntity<StatusResponse>
}