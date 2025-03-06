package application.request

data class RegisterAdminRequest(
    val login: String,
    val password: String,
    val name: String? = null,
    val secret: String
)