package application.response

import application.entity.User

data class GetNotRegisteredResponse (
    val users : List<User>
)