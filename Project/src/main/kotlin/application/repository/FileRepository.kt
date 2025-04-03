package application.repository

import application.entity.File
import application.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface FileRepository : JpaRepository<File, Long> {
    fun findByUserAndIsPrinted(user: User, isPrinted: Boolean): List<File>

    fun findByUserAndFilePath(user: User, filePath: String): File?
}