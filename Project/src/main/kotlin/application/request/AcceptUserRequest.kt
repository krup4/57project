package application.request

import jakarta.validation.constraints.Size

data class AcceptUserRequest (
    @field:Size(min = 3, max = 20, message = "Login must be between 3 and 20 characters")
    val login : String,

    var isConfirmed: Boolean
)