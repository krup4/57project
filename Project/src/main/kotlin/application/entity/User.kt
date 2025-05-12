package application.entity

import jakarta.persistence.*
import java.util.*


@Entity
@Table(
    name = "users",
    indexes = [
        Index(name = "idx_users_login", columnList = "login"),
        Index(name = "idx_users_token", columnList = "token")
    ]
)
class User (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var uuid: UUID,

    @Column(nullable = false)
    val login: String,

    @Column(nullable = false)
    var password: String,

    @Column(nullable = true)
    var name: String? = null,

    @Column(nullable = false)
    val isAdmin: Boolean = false,

    @Column(nullable = true)
    var token: String? = null,

    @Column(nullable = false)
    var isConfirmed: Boolean = false
)