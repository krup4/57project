package application.entity

import jakarta.persistence.*


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
    val login: String,

    @Column(nullable = false)
    val password: String,

    @Column(nullable = true)
    val name: String? = null,

    @Column(nullable = false)
    val isAdmin: Boolean = false,

    @Column(nullable = true)
    val token: String? = null,

    @Column(nullable = false)
    val isRegistered: Boolean = false
)