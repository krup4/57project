package application.service

import application.config.Properties
import application.entity.User
import application.exception.*
import application.repository.UserRepository
import application.request.AcceptUserRequest
import application.request.AuthoriseRequest
import application.request.RegisterAdminRequest
import application.request.SignUpRequest
import application.response.AcceptUserResponse
import application.response.AuthoriseResponse
import application.response.GetNotRegisteredResponse
import application.response.StatusResponse
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val properties: Properties
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun registerUser(request: SignUpRequest): StatusResponse {
        val user = userRepository.findByLogin(request.login)

        if (user != null) {
            throw UserIsAlreadyExistsException("User is already exists")
        }

        val hashPassword = passwordEncoder.encode(request.password)

        userRepository.save(
            User(
                login = request.login,
                password = hashPassword,
                name = request.name,
                isAdmin = false,
                isRegistered = false
            )
        )

        return StatusResponse("ok")
    }

    @Transactional
    fun authUser(request: AuthoriseRequest): AuthoriseResponse {
        val user = userRepository.findByLogin(request.login)

        if (user == null) {
            throw UserNotFoundException("User not found")
        }

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw BadRequestException("Password is incorrect")
        }

        if (!user.isRegistered) {
            throw BadRequestException("User registration was not confirmed")
        }

        val token = jwtService.generateToken(
            mapOf(
                "login" to user.login,
                "name" to user.name
            ),
            user.login
        )
        user.token = token
        userRepository.save(user)

        return AuthoriseResponse(token = token)
    }

    fun getNotRegistered(token: String): GetNotRegisteredResponse {
        val user = userRepository.findByToken(token)

        if (user == null) {
            throw UserNotFoundException("token is invalid")
        }

        if (!user.isAdmin) {
            throw UserIsNotAdminException("User is not admin")
        }

        val notRegisteredUsers = userRepository.findByIsRegistered(isRegistered = false)
        return GetNotRegisteredResponse(notRegisteredUsers)
    }

    @Transactional
    fun registerAdmin(request: RegisterAdminRequest): StatusResponse {
        val user = userRepository.findByLogin(request.login)

        if (user != null) {
            throw UserIsAlreadyExistsException("User is already exists")
        }

        if (request.secret != properties.secret) {
            throw BadRequestException("Invalid secret")
        }

        val hashPassword = passwordEncoder.encode(request.password)

        userRepository.save(
            User(
                login = request.login,
                password = hashPassword,
                name = request.name,
                isAdmin = true,
                isRegistered = true
            )
        )

        return StatusResponse("ok")

    }

    @Transactional
    fun acceptUser(request: AcceptUserRequest, token: String): StatusResponse {
        val user = userRepository.findByToken(token)

        if (user == null) {
            throw UserNotFoundException("token is invalid")
        }

        if (user.isAdmin) {
            throw UserIsAdminException("User is admin")
        }

        val requestUser = userRepository.findByLogin(request.login)

        if (requestUser == null) {
            throw UserNotFoundException("User is not found")
        }

        if (request.isConfirmed) {
            requestUser.isRegistered = true
            userRepository.save(requestUser)
        }
        else {
            userRepository.delete(requestUser)
        }

        return StatusResponse("ok")

    }
}