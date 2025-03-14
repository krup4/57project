package application.service

import application.entity.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtService {
    private val secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)

    fun generateToken(claims: Map<String?, Any?>, subject: String, expirationTime: Long = 1000 * 60 * 60): String {
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + expirationTime))
            .signWith(secretKey)
            .compact()
    }
}