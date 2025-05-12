package application.request

data class AcceptUserRequest (
    val login : String,
    var isConfirmed: Boolean
)