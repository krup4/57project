package application.repository

import jakarta.persistence.*


@Entity
@Table(name = "files")
class FileRepository (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne // Связь многие-к-одному с таблицей users
    @JoinColumn(name = "user_id", nullable = false) // Внешний ключ
    val user: UserRepository, // Ссылка на пользователя

    @Column(nullable = false)
    val filePath: String,

    @Column(nullable = false)
    val isPrinted: Boolean = false
)