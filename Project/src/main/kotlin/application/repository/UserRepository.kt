package application.repository

import application.entity.User
import jdk.jfr.Registered
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByLogin(login: String): User?

    fun findByIsConfirmed(isConfirmed: Boolean): List<User>

    fun findByToken(token: String): User?

}