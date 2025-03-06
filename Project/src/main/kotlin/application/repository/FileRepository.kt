package application.repository

import application.entity.File
import org.springframework.data.jpa.repository.JpaRepository

interface FileRepository : JpaRepository<File, Long> {}