package dasturlash.uz.warehouse_management.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtUtil {

    private val SECRET_KEY: SecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)

    fun generateToken(userDetails: UserDetails): String =
        Jwts.builder()
            .setSubject(userDetails.username)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 soat
            .signWith(SECRET_KEY)
            .compact()

    fun extractUsername(token: String): String =
        Jwts.parserBuilder()
            .setSigningKey(SECRET_KEY)
            .build()
            .parseClaimsJws(token)
            .body
            .subject

    fun validateToken(token: String, userDetails: UserDetails): Boolean =
        extractUsername(token) == userDetails.username
}
