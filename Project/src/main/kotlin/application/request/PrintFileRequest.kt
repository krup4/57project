package application.request

import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile

data class PrintFileRequest(
    @RequestPart
    val file: MultipartFile
)
