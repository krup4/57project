package application.controller

import application.request.SignUpRequest
import application.service.RegisterService
import org.springframework.beans.factory.annotation.Autowired
import application.request.AcceptUserRequest
import application.request.RegAdminRequest
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user")
class UserRegister(@Autowired private val registerService: RegisterService) {

    @PostMapping("/register")
    fun regUser(@RequestBody request: SignUpRequest) = registerService.regUser(request)

    @PostMapping("/authorize")
    fun authUser(@RequestParam login: String, @RequestParam password: String) = registerService.authUser(login, password)

    @GetMapping("/get_users")
    fun getNotRegisteredUsers(@RequestHeader("Authorization") token: String) = registerService.getNotRegistered(token)

    @PostMapping("/reg_admin")
    fun regAdmin(@RequestBody request: RegAdminRequest) = registerService.regAdmin(request)

    @PostMapping("/accept_user")
    fun acceptUser(@RequestBody request: AcceptUserRequest, @RequestHeader("Authorization") token: String) = registerService.acceptUser(request, token)
}