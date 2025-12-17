package dasturlash.uz.warehouse_management

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDate


class WarehouseCreateDTO(
    val name: String,
    val address: String
)

class WarehouseUpdateDTO(
    val name: String,
    val address: String,
    val status: Boolean? = null
)

class WarehouseDTO(
    val id: String,
    val name: String,
    val address: String,
    val status: Boolean
)


class EmployeeCreateDTO(
    val firstName: String,
    val lastName: String,
    val phoneNumber: String, // +998 bilan boshlanishi majbur
    val password: String,
    val warehouseId: String
)


class EmployeeUpdateDTO(
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val status: Boolean
)


class EmployeeDTO(
    val id: String?,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val employeeCode: String,
    val warehouseId: String,
    val status: Boolean
)


class CategoryCreateDTO(
    val name: String,
    val parentId: String? = null
)

class CategoryUpdateDTO(
    val name: String,
    val parentId: String? = null,
    val status: Boolean
)


class CategoryDTO(
    val id: String,
    val name: String,
    val parentId: String?,
    val status: Boolean
)




class MeasurementCreateDTO(
    val name: String
)

class MeasurementUpdateDTO(
    val name: String,
    val status: Boolean
)

class MeasurementResponseDTO(
    val id: String,
    val name: String,
    val status: Boolean
)








class CurrencyDTO(
    val id: String?,
    val name: String,
    val status: Boolean
)





class ProductImageDTO(
    val id: String? = null,
    val productId: String,
    val originName: String,
    val contentType: String,
    val path: String,
    val status: Boolean = true
)

class ProductCreateDTO(
    val name: String,
    val categoryId: String,
    val measurementId: String
)


class ProductUpdateDTO(
    val name: String,
    val categoryId: String,
    val measurementId: String,
    val status: Boolean
)


class ProductResponseDTO(
    val id: String?,
    val name: String,
    val uniqueNumber: String,
    val categoryId: String,
    val measurementId: String,
    val status: Boolean
)






class SupplierCreateDTO(
    val name: String,
    val phoneNumber: String
)

class SupplierUpdateDTO(
    val name: String,
    val phoneNumber: String,
    val status: Boolean
)


class SupplierResponseDTO(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val status: Boolean
)



class StockInDTO(
    val id: String?,
    val date: LocalDate,
    val warehouseId: String,
    val supplierId: String,
    val currencyId: String,
    val documentNumber: String,
    val invoiceNumber: String,
    val status: Boolean
)


class StockOutDTO(
    val id: String?,
    val date: LocalDate,
    val warehouseId: String,
    val currencyId: String,
    val documentNumber: String,
    val invoiceNumber: String,
    val status: Boolean
)


class StockInItemDTO(
    val id: String?,
    val stockInId: String,
    val productId: String,
    val quantity: Double,
    val inPrice: Double,
    val salePrice: Double,
    val expireDate: LocalDate?,
    val status: Boolean
)


class StockOutItemDTO(
    val id: String?,
    val stockOutId: String,
    val stockInItemId: String,
    val quantity: Double,
    val outPrice: Double,
    val status: Boolean
)





class LoginRequest(
    val phoneNumber: String,
    val password: String
)

class LoginResponse(
    val token: String
)


//class UserDetailsResponse(
//    val id: String,
//    private val phoneNumber: String,
//    val firstName: String,
//    val lastName: String?,
//    val role: Role,
//    private val password: String
//) : UserDetails {
//
//    override fun getAuthorities(): Collection<GrantedAuthority> =
//        listOf(SimpleGrantedAuthority("ROLE_${role.name}"))
//
//    override fun getPassword(): String = password
//
//    override fun getUsername(): String = phoneNumber
//
//    override fun isAccountNonExpired(): Boolean = true
//    override fun isAccountNonLocked(): Boolean = true
//    override fun isCredentialsNonExpired(): Boolean = true
//    override fun isEnabled(): Boolean = true
//}


