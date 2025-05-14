package application.response

data class AuthoriseResponse(
    val token: String,
    val isAdmin: Boolean
)
