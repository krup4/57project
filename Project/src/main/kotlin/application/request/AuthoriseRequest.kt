package application.request

data class AuthoriseRequest(
    val login: String,
    val password: String
)
