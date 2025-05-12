package application.entity

import jakarta.persistence.*
import java.util.UUID


@Entity
@Table(
    name = "files"
)
class File (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne // Связь многие-к-одному с таблицей users
    @JoinColumn(name = "user_id", nullable = false) // Внешний ключ
    val user: User, // Ссылка на пользователя

    @Column(nullable = false)
    var uuid: UUID,

    @Column(nullable = false)
    val filePath: String,

    @Column(nullable = false)
    var isPrinted: Boolean = false
)