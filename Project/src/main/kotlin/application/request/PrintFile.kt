package application.request

import org.springframework.web.multipart.MultipartFile

data class PrintFile(
    val file: MultipartFile
)
