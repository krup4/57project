package application.request

data class SignUpRequest(
    val login: String,
    val password: String,
    val name: String? = null
)