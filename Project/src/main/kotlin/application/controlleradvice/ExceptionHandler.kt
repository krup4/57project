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
        logger.warn("Invalid incoming request")
        return ResponseEntity.badRequest().body(StatusResponse(message = exception.message ?: "Arguments are not valid"))
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFound(exception: UserNotFoundException): ResponseEntity<StatusResponse> {
        logger.warn("User not found")
        return ResponseEntity.status(404).body(StatusResponse(message = exception.message ?: "User not found"))
    }

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequest(exception: BadRequestException): ResponseEntity<StatusResponse> {
        logger.warn("Bad request")
        return ResponseEntity.badRequest().body(StatusResponse(message = exception.message ?: "Bad request"))
    }

    @ExceptionHandler(UserIsAlreadyExistsException::class)
    fun handleUserIsAlreadyExists(exception: UserIsAlreadyExistsException): ResponseEntity<StatusResponse> {
        logger.warn("User is already exists")
        return ResponseEntity.badRequest().body(StatusResponse(message = exception.message ?: "User is already exists"))
    }

    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorized(exception: UnauthorizedException): ResponseEntity<StatusResponse> {
        logger.warn("User is unauthorized")
        return ResponseEntity.status(401).body(StatusResponse(message = exception.message ?: "Unauthorized"))
    }

    @ExceptionHandler(IncorrectFileException::class)
    fun handleIncorrectFile(exception: IncorrectFileException): ResponseEntity<StatusResponse> {
        logger.warn("File is incorrect")
        return ResponseEntity.badRequest().body(StatusResponse(message = exception.message ?: "Incorrect file"))
    }
}