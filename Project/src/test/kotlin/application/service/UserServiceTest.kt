package application.service

import application.config.Properties
import application.entity.User
import application.repository.UserRepository
import application.request.RegisterAdminRequest
import application.request.SignUpRequest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder

@SpringBootTest(
    properties = ["spring.profiles.active=test"]
)
class UserServiceTest (
    @Autowired
    val userRepository: UserRepository,

    @Autowired
    val passwordEncoder: PasswordEncoder,

    @Autowired
    val jwtService: JwtService,

    @Autowired
    val properties: Properties
) {
    val userService = UserService(userRepository, passwordEncoder, jwtService, properties)

    //пишите сюда тесты
}