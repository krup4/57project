package application.service

import application.request.AcceptUserRequest
import application.request.RegAdminRequest
import application.request.SignUpRequest
import application.response.AcceptUserResponse
import application.response.AuthoriseResponse
import application.response.GetNotRegisteredResponse
import application.response.StatusResponse
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class RegisterService {

    fun regUser(request: SignUpRequest): ResponseEntity<StatusResponse> {
        TODO()
    }

    fun authUser(login: String, password: String): ResponseEntity<AuthoriseResponse> {
        TODO()
    }

    fun getNotRegistered(token: String): ResponseEntity<GetNotRegisteredResponse> {
        TODO()
    }

    fun regAdmin(request: RegAdminRequest): ResponseEntity<StatusResponse> {
        TODO()
    }

    fun acceptUser(request: AcceptUserRequest, token: String): ResponseEntity<AcceptUserResponse> {
        TODO()
    }
}