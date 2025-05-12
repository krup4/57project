package application.controller

import application.request.SignUpRequest
import application.service.UserService
import application.request.AcceptUserRequest
import application.request.AuthoriseRequest
import application.request.RegisterAdminRequest
import application.response.StatusResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/user")
class UserController(
    private val userService: UserService
) {

    @PostMapping("/register")
    fun registerUser(@RequestBody request: SignUpRequest): StatusResponse {
        val user = userService.registerUser(request)
        return StatusResponse("ok")
    }

    @PostMapping("/authorize")
    fun authUser(@Valid @RequestBody request: AuthoriseRequest) = userService.authUser(request)

    @GetMapping("/get_users")
    fun getNotRegisteredUsers(@RequestHeader("Authorization") token: String) = userService.getNotRegistered(token)

    @PostMapping("/reg_admin")
    fun registerAdmin(@Valid @RequestBody request: RegisterAdminRequest): StatusResponse {
        val admin = userService.registerAdmin(request)
        return StatusResponse("ok")
    }

    @PostMapping("/accept_user")
    fun acceptUser(@RequestBody request: AcceptUserRequest, @RequestHeader("Authorization") token: String) = userService.acceptUser(request, token)
}