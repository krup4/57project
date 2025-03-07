package application.controlleradvice

import application.exception.BadRequestException
import application.exception.UserNotFoundException
import application.response.StatusResponse
import io.swagger.v3.oas.annotations.Hidden
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@Hidden
@RestControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleArgumentNotValid(exception: MethodArgumentNotValidException) =
        ResponseEntity.badRequest().body(StatusResponse(message = exception.message ?: "Arguments are not valid"))

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFound(exception: UserNotFoundException) =
        ResponseEntity.status(404).body(StatusResponse(message = exception.message ?: "User not found"))

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequest(exception: BadRequestException) =
        ResponseEntity.badRequest().body(StatusResponse(message = exception.message ?: "Bad request"))
}