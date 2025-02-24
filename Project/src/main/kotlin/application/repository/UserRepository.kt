package application.repository

import jakarta.persistence.*


@Entity
@Table(name = "files")
class UserRepository (

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

    @Column(nullable = false)
    val token: String
)