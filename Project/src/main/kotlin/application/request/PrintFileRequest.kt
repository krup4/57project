package application.request

import org.springframework.web.multipart.MultipartFile

data class PrintFileRequest(
    val file: MultipartFile
)
