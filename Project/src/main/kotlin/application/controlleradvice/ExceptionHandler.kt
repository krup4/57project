package application.controlleradvice

import application.exception.*
import application.response.StatusResponse
import io.swagger.v3.oas.annotations.Hidden
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@Hidden
@RestControllerAdvice
class ExceptionHandler {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleArgumentNotValid(exception: MethodArgumentNotValidException): ResponseEntity<StatusResponse> {
        logger.warn(exception.message)
        return ResponseEntity.badRequest().body(StatusResponse(message = exception.message ?: "Arguments are not valid"))
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFound(exception: UserNotFoundException): ResponseEntity<StatusResponse> {
        logger.warn(exception.message)
        return ResponseEntity.status(404).body(StatusResponse(message = exception.message ?: "User not found"))
    }

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequest(exception: BadRequestException): ResponseEntity<StatusResponse> {
        logger.warn(exception.message)
        return ResponseEntity.badRequest().body(StatusResponse(message = exception.message ?: "Bad request"))
    }

    @ExceptionHandler(UserIsAlreadyExistsException::class)
    fun handleUserIsAlreadyExists(exception: UserIsAlreadyExistsException): ResponseEntity<StatusResponse> {
        logger.warn(exception.message)
        return ResponseEntity.badRequest().body(StatusResponse(message = exception.message ?: "User is already exists"))
    }

    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorized(exception: UnauthorizedException): ResponseEntity<StatusResponse> {
        logger.warn(exception.message)
        return ResponseEntity.status(401).body(StatusResponse(message = exception.message ?: "Unauthorized"))
    }

    @ExceptionHandler(IncorrectFileException::class)
    fun handleIncorrectFile(exception: IncorrectFileException): ResponseEntity<StatusResponse> {
        logger.warn(exception.message)
        return ResponseEntity.badRequest().body(StatusResponse(message = exception.message ?: "Incorrect file"))
    }

    @ExceptionHandler(UserIsNotAdminException::class)
    fun handleUserIsNotAdmin(exception: UserIsNotAdminException): ResponseEntity<StatusResponse> {
        logger.warn(exception.message)
        return ResponseEntity.badRequest().body(StatusResponse(message = exception.message ?: "User is not admin"))
    }

    @ExceptionHandler(ClientErrorException::class)
    fun handleClientError(exception: ClientErrorException): ResponseEntity<StatusResponse> {
        logger.warn(exception.message)
        return ResponseEntity.status(500).body(StatusResponse(message = exception.message ?: "Client error"))
    }
}