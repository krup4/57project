package application.entity

import jakarta.persistence.*


@Entity
@Table(name = "users")
class User (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

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
    var isRegistered: Boolean = false
)