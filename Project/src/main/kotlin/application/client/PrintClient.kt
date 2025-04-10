package application.client

import application.request.PrintFileRequest
import application.response.StatusResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile

@FeignClient(name = "printClient", url = "paste_url_there")
interface PrintClient {

    @PostMapping(path= ["/print"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun printFile(@RequestPart("file") file: MultipartFile): ResponseEntity<StatusResponse>
}