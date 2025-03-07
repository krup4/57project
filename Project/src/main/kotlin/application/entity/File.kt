package application.entity

import jakarta.persistence.*


@Entity
@Table(
    name = "files",
    indexes = [
        Index(name = "idx_user", columnList = "user")
    ]
)
class File (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne // Связь многие-к-одному с таблицей users
    @JoinColumn(name = "user_id", nullable = false) // Внешний ключ
    val user: User, // Ссылка на пользователя

    @Column(nullable = false)
    val filePath: String,

    @Column(nullable = false)
    var isPrinted: Boolean = false
)