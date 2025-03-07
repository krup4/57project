package application.request

import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class AuthoriseRequest(
    @field:Size(min = 3, max = 20, message = "Login must be between 3 and 20 characters")
    val login: String,

    @field:Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$",
        message = "Password must contain at least 8 characters, one digit, one lowercase and one uppercase letter"
    )
    val password: String
)