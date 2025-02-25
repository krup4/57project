package application.response

import application.dto.User

data class GetNotRegisteredResponse (
    val users : List<User>
)