package application.dto

data class User(
    val login: String,
    val password: String,
    val name: String? = null
)
