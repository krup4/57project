package application.service

import application.entity.User
import application.exception.BadRequestException
import application.exception.UserIsAlreadyExistsException
import application.exception.UserNotFoundException
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
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService
) {

    fun registerUser(request: SignUpRequest): ResponseEntity<StatusResponse> {
        TODO()
    }

    fun authUser(request: AuthoriseRequest): ResponseEntity<AuthoriseResponse> {
        val user = userRepository.findByLogin(request.login)

        if (user == null) {
            throw UserNotFoundException("User not found")
        }

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw BadRequestException("Password is incorrect")
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

        return ResponseEntity.status(200).body(AuthoriseResponse(token = token))
    }

    fun getNotRegistered(token: String): ResponseEntity<GetNotRegisteredResponse> {
        TODO()
    }

    @Transactional
    fun registerAdmin(request: RegisterAdminRequest): ResponseEntity<StatusResponse> {
        val user = userRepository.findByLogin(request.login)

        if (user != null) {
            throw UserIsAlreadyExistsException("User is already exists")
        }

        val secret = System.getenv("SECRET")

        if (request.secret != secret) {
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

        return ResponseEntity.status(200).body(StatusResponse("ok"))
    }

    fun acceptUser(request: AcceptUserRequest, token: String): ResponseEntity<AcceptUserResponse> {
        TODO()
    }
}