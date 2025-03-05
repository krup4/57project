package application.request

data class RegAdminRequest(
    val login: String,
    val password: String,
    val name: String? = null,
    val secret: String
)