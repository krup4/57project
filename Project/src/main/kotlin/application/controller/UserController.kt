package application.controller

import application.request.SignUpRequest
import application.service.UserService
import application.request.AcceptUserRequest
import application.request.AuthoriseRequest
import application.request.RegisterAdminRequest
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/user")
class UserController(
    private val userService: UserService
) {

    @PostMapping("/register")
    fun registerUser(@RequestBody request: SignUpRequest) = userService.registerUser(request)

    @PostMapping("/authorize")
    fun authUser(@Valid @RequestBody request: AuthoriseRequest) = userService.authUser(request)

    @GetMapping("/get_users")
    fun getNotRegisteredUsers(@RequestHeader("Authorization") token: String) = userService.getNotRegistered(token)

    @PostMapping("/reg_admin")
    fun registerAdmin(@Valid @RequestBody request: RegisterAdminRequest) = userService.registerAdmin(request)

    @PostMapping("/accept_user")
    fun acceptUser(@RequestBody request: AcceptUserRequest, @RequestHeader("Authorization") token: String) = userService.acceptUser(request, token)
}