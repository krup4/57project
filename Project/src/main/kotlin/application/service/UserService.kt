package application.service

import application.config.Properties
import application.entity.User
import application.exception.*
import application.repository.UserRepository
import application.request.AcceptUserRequest
import application.request.AuthoriseRequest
import application.request.RegisterAdminRequest
import application.request.SignUpRequest
import application.response.AuthoriseResponse
import application.response.GetNotRegisteredResponse
import application.response.StatusResponse
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val properties: Properties
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun registerUser(request: SignUpRequest): User {
        val user = userRepository.findByLogin(request.login)

        if (user != null) {
            throw UserIsAlreadyExistsException("User is already exists")
        }


        val hashPassword = passwordEncoder.encode(request.password)

        val newUser = userRepository.save(
            User(
                login = request.login,
                password = hashPassword,
                name = request.name,
                isAdmin = false,
                isConfirmed = false,
                uuid = UUID.randomUUID()
            )
        )

        logger.info("Зарегистрирован пользователь ${newUser.uuid}; login: ${newUser.login}, name: ${newUser.name}, isAdmin: ${newUser.isAdmin}, isConfirmed: ${newUser.isConfirmed}")

        return newUser
    }

    @Transactional
    fun authUser(request: AuthoriseRequest): AuthoriseResponse {

        val user = userRepository.findByLogin(request.login)

        if (user == null) {
            throw UserNotFoundException("User not found")
        }

        logger.debug("Найден пользователь ${user.uuid} по логину : ${user.login}, id : ${user.id}")

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw BadRequestException("Password is incorrect")
        }

        if (!user.isConfirmed) {
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
        logger.info("Авторизация пользователя ${user.uuid} прошла успешно")
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

        val notRegisteredUsers = userRepository.findByIsConfirmed(isConfirmed = false)
        logger.info("Получен список неподтвержденных пользователей")
        return GetNotRegisteredResponse(notRegisteredUsers)
    }

    @Transactional
    fun registerAdmin(request: RegisterAdminRequest): User {
        val user = userRepository.findByLogin(request.login)

        if (user != null) {
            logger.debug("Логин пользователя ${user.uuid}: ${user.login}")
            throw UserIsAlreadyExistsException("User is already exists")
        }

        if (request.secret != properties.secret) {
            logger.debug("Секрет : ${request.secret}")
            throw BadRequestException("Invalid secret")
        }

        val hashPassword = passwordEncoder.encode(request.password)

        val newAdmin = userRepository.save(
            User(
                login = request.login,
                password = hashPassword,
                name = request.name,
                isAdmin = true,
                isConfirmed = true,
                uuid = UUID.randomUUID()
            )
        )
        logger.debug("Сохранен админ ${newAdmin.uuid} login : ${request.login}, name : ${request.name}, isAdmin : ${true}, isRegistered : ${true}")
        logger.info("Сохранен админ ${newAdmin.uuid}")
        return newAdmin

    }

    @Transactional
    fun acceptUser(request: AcceptUserRequest, token: String): StatusResponse {
        val user = userRepository.findByToken(token)

        if (user == null) {
            throw UserNotFoundException("token is invalid")
        }

        if (!user.isAdmin) {
            throw UserIsNotAdminException("User is not admin")
        }

        val requestUser = userRepository.findByLogin(request.login)

        if (requestUser == null) {
            throw UserNotFoundException("User is not found")
        }

        if (request.isConfirmed) {
            requestUser.isConfirmed = true
            userRepository.save(requestUser)
            logger.info("Регистрация пользователя ${requestUser.uuid} подтверждена")
        }
        else {
            userRepository.delete(requestUser)
            logger.info("Регистрация пользователя ${requestUser.uuid} не подтверждена")
        }

        return StatusResponse("ok")

    }
}