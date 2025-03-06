package application.service

import application.request.AcceptUserRequest
import application.request.AuthoriseRequest
import application.request.RegisterAdminRequest
import application.request.SignUpRequest
import application.response.AcceptUserResponse
import application.response.AuthoriseResponse
import application.response.GetNotRegisteredResponse
import application.response.StatusResponse
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class UserService {

    fun registerUser(request: SignUpRequest): ResponseEntity<StatusResponse> {
        TODO()
    }

    fun authUser(request: AuthoriseRequest): ResponseEntity<AuthoriseResponse> {
        TODO()
    }

    fun getNotRegistered(token: String): ResponseEntity<GetNotRegisteredResponse> {
        TODO()
    }

    fun registerAdmin(request: RegisterAdminRequest): ResponseEntity<StatusResponse> {
        TODO()
    }

    fun acceptUser(request: AcceptUserRequest, token: String): ResponseEntity<AcceptUserResponse> {
        TODO()
    }
}