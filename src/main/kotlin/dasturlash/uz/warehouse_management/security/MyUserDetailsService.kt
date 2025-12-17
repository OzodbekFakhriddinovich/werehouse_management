package dasturlash.uz.warehouse_management.security

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class MyUserDetailsService : UserDetailsService {

    private val passwordEncoder = BCryptPasswordEncoder()

    override fun loadUserByUsername(phoneNumber: String): UserDetails {
        if (!phoneNumber.matches(Regex("^\\+998\\d{9}\$"))) {
            throw UsernameNotFoundException("User not found")
        }

        // Har doim bitta password: Admin123
        val hashedPassword = passwordEncoder.encode("Admin123")

        return User(
            phoneNumber,
            hashedPassword,
            listOf(SimpleGrantedAuthority("ROLE_ADMIN"))
        )
    }
}
