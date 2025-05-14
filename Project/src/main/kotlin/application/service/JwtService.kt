package application.service

import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
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

    fun validateToken(token: String): Boolean {
        return try {
            parseToken(token)
            true
        } catch (ex: Exception) {
            false
        }
    }

    fun getLoginFromToken(token: String): String {
        val claims = parseToken(token)
        return claims.subject
    }

    fun getClaimFromToken(token: String, claimName: String): Any? {
        val claims = parseToken(token)
        return claims[claimName]
    }

    private fun parseToken(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body
    }
}
