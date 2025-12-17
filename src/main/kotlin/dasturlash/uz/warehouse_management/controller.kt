package dasturlash.uz.warehouse_management


import dasturlash.uz.warehouse_management.security.JwtUtil
import dasturlash.uz.warehouse_management.security.MyUserDetailsService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*



@RestController
@RequestMapping("/api/auth")
@Tag(name = "authorize", description = "Autorizatsiyadan o'tib jwt token olish")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val userDetailsService: MyUserDetailsService,
    private val jwtUtil: JwtUtil,
    private val passwordEncoder: PasswordEncoder
) {

    @PostMapping("/login")
    @Operation(summary = "login orqali jwt token olish")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<LoginResponse> {
        // Authentication
        val userDetails = userDetailsService.loadUserByUsername(request.phoneNumber)

        if (!passwordEncoder.matches(request.password, userDetails.password)) {
            return ResponseEntity.status(403).build()
        }

        val token = jwtUtil.generateToken(userDetails)
        return ResponseEntity.ok(LoginResponse(token))
    }
}


@RestController
@RequestMapping("/api/warehouses")
@Tag(name = "Warehouse", description = "Ombor CRUD operatsiyalari")
class WarehouseController(private val service: WarehouseService) {

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Yangi ombor yaratish")
    fun create(@RequestBody dto: WarehouseCreateDTO): ResponseEntity<WarehouseDTO> =
        ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto))

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE')")
    @Operation(summary = "Bitta omborni olish")
    fun getOne(@PathVariable id: String): ResponseEntity<WarehouseDTO> =
        ResponseEntity.ok(service.getOne(id))


    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE')")
    @Operation(summary = "Barcha omborlarni olish")
    fun getAll(): ResponseEntity<List<WarehouseDTO>> =
        ResponseEntity.ok(service.getAll())


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Omborni yangilash")
    fun update(@PathVariable id: String, @RequestBody dto: WarehouseUpdateDTO): ResponseEntity<WarehouseDTO> =
        ResponseEntity.ok(service.update(id, dto))

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Omborni o‘chirish (soft delete)")
    fun delete(@PathVariable id: String): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }
}



@RestController
@RequestMapping("/api/employees")
@Tag(name = "Employee", description = "Ishchi CRUD operatsiyalari")
class EmployeeController(
    private val service: EmployeeService
) {

    // CREATE
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Yangi ishchi yaratish")
    fun create(
        @RequestBody dto: EmployeeCreateDTO
    ): ResponseEntity<EmployeeDTO> =
        ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto))

    // GET ONE
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(summary = "Bitta ishchini olish")
    fun getOne(@PathVariable id: String): ResponseEntity<EmployeeDTO> =
        ResponseEntity.ok(service.getOne(id))

    // GET ALL
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(summary = "Barcha ishchilarni olish")
    fun getAll(): ResponseEntity<List<EmployeeDTO>> =
        ResponseEntity.ok(service.getAll())

    // UPDATE
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Ishchini yangilash")
    fun update(
        @PathVariable id: String,
        @RequestBody dto: EmployeeUpdateDTO
    ): ResponseEntity<EmployeeDTO> =
        ResponseEntity.ok(service.update(id, dto))

    // DELETE (SOFT)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Ishchini o‘chirish (soft delete)")
    fun delete(@PathVariable id: String): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }
}





@RestController
@RequestMapping("/api/categories")
@Tag(name = "Category", description = "Kategoriya CRUD operatsiyalari")
class CategoryController(private val service: CategoryService) {

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun create(@RequestBody dto: CategoryCreateDTO): ResponseEntity<CategoryDTO> =
        ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto))

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE')")
    fun getOne(@PathVariable id: String): ResponseEntity<CategoryDTO> =
        ResponseEntity.ok(service.getOne(id))


    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE')")
    fun getAll(): ResponseEntity<List<CategoryDTO>> =
        ResponseEntity.ok(service.getAll())

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun update(
        @PathVariable id: String,
        @RequestBody dto: CategoryUpdateDTO
    ): ResponseEntity<CategoryDTO> =
        ResponseEntity.ok(service.update(id, dto))

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun delete(@PathVariable id: String): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }
}


@RestController
@RequestMapping("/api/measurements")
@Tag(name = "Measurement", description = "O'lchov birligi CRUD operatsiyalari")
class MeasurementController(private val service: MeasurementService) {

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun create(@RequestBody dto: MeasurementCreateDTO): MeasurementResponseDTO =
        service.create(dto)

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE')")
    fun getOne(@PathVariable id: String): MeasurementResponseDTO =
        service.getOne(id)

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE')")
    fun getAll(): List<MeasurementResponseDTO> =
        service.getAll()

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun update(@PathVariable id: String, @RequestBody dto: MeasurementUpdateDTO): MeasurementResponseDTO =
        service.update(id, dto)

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun delete(@PathVariable id: String): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }
}







@RestController
@RequestMapping("/api/products")
@Tag(name = "Product", description = "Mahsulot CRUD operatsiyalari")
class ProductController(
    private val service: ProductService
) {

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Yangi mahsulot yaratish")
    fun create(
        @RequestBody dto: ProductCreateDTO
    ): ResponseEntity<ProductResponseDTO> =
        ResponseEntity
            .status(HttpStatus.CREATED)
            .body(service.create(dto))


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE')")
    @Operation(summary = "Bitta mahsulotni olish")
    fun getOne(
        @PathVariable id: String
    ): ResponseEntity<ProductResponseDTO> =
        ResponseEntity.ok(service.getOne(id))


    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE')")
    @Operation(summary = "Barcha mahsulotlarni olish")
    fun getAll(): ResponseEntity<List<ProductResponseDTO>> =
        ResponseEntity.ok(service.getAll())


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mahsulotni yangilash")
    fun update(
        @PathVariable id: String,
        @RequestBody dto: ProductUpdateDTO
    ): ResponseEntity<ProductResponseDTO> =
        ResponseEntity.ok(service.update(id, dto))


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mahsulotni o‘chirish (soft delete)")
    fun delete(
        @PathVariable id: String
    ): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }
}





@RestController
@RequestMapping("/api/suppliers")
@Tag(name = "Supplier", description = "Taminotchi CRUD operatsiyalari")
class SupplierController(private val service: SupplierService) {

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Yangi taminotchi yaratish")
    fun create(@RequestBody dto: SupplierCreateDTO): ResponseEntity<SupplierResponseDTO> =
        ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto))


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE')")
    @Operation(summary = "Bitta taminotchini olish")
    fun getOne(@PathVariable id: String): ResponseEntity<SupplierResponseDTO> =
        ResponseEntity.ok(service.getOne(id))

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE')")
    @Operation(summary = "Barcha taminotchilarni olish")
    fun getAll(): ResponseEntity<List<SupplierResponseDTO>> =
        ResponseEntity.ok(service.getAll())

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Taminotchini yangilash")
    fun update(
        @PathVariable id: String,
        @RequestBody dto: SupplierUpdateDTO
    ): ResponseEntity<SupplierResponseDTO> =
        ResponseEntity.ok(service.update(id, dto))

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Taminotchini o‘chirish (soft delete)")
    fun delete(@PathVariable id: String): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }
}



@RestController
@RequestMapping("/api/stock-in")
@Tag(name = "StockIn", description = "Omborga kirim CRUD operatsiyalari")
class StockInController(private val service: StockInService) {

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Yangi kirim yaratish")
    fun create(@RequestBody dto: StockInDTO): ResponseEntity<StockInDTO> =
        ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto))

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(summary = "Bitta kirimni olish")
    fun getOne(@PathVariable id: String): ResponseEntity<StockInDTO> =
        ResponseEntity.ok(service.getOne(id))

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(summary = "Barcha kirimlarni olish")
    fun getAll(): ResponseEntity<List<StockInDTO>> =
        ResponseEntity.ok(service.getAll())

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Kirimni yangilash")
    fun update(@PathVariable id: String, @RequestBody dto: StockInDTO): ResponseEntity<StockInDTO> =
        ResponseEntity.ok(service.update(id, dto))

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Kirimni o‘chirish (soft delete)")
    fun delete(@PathVariable id: String): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }
}



@RestController
@RequestMapping("/api/stock-out")
@Tag(name = "StockOut", description = "Ombordan chiqim CRUD operatsiyalari")
class StockOutController(private val service: StockOutService) {

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Yangi chiqim yaratish")
    fun create(@RequestBody dto: StockOutDTO): ResponseEntity<StockOutDTO> =
        ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto))

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(summary = "Bitta chiqimni olish")
    fun getOne(@PathVariable id: String): ResponseEntity<StockOutDTO> =
        ResponseEntity.ok(service.getOne(id))

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(summary = "Barcha chiqimlarni olish")
    fun getAll(): ResponseEntity<List<StockOutDTO>> =
        ResponseEntity.ok(service.getAll())

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Chiqimni yangilash")
    fun update(@PathVariable id: String, @RequestBody dto: StockOutDTO): ResponseEntity<StockOutDTO> =
        ResponseEntity.ok(service.update(id, dto))

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Chiqimni o‘chirish (soft delete)")
    fun delete(@PathVariable id: String): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }
}



@RestController
@RequestMapping("/api/stock-in-items")
@Tag(name = "StockInItem", description = "Kirimdagi mahsulotlar CRUD")
class StockInItemController(private val service: StockInItemService) {

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Yangi kirim mahsuloti yaratish")
    fun create(@RequestBody dto: StockInItemDTO): ResponseEntity<StockInItemDTO> =
        ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto))

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(summary = "Bitta kirim mahsulotini olish")
    fun getOne(@PathVariable id: String): ResponseEntity<StockInItemDTO> =
        ResponseEntity.ok(service.getOne(id))

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(summary = "Barcha kirim mahsulotlarini olish")
    fun getAll(): ResponseEntity<List<StockInItemDTO>> =
        ResponseEntity.ok(service.getAll())

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Kirim mahsulotini yangilash")
    fun update(@PathVariable id: String, @RequestBody dto: StockInItemDTO): ResponseEntity<StockInItemDTO> =
        ResponseEntity.ok(service.update(id, dto))

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Kirim mahsulotini o‘chirish (soft delete)")
    fun delete(@PathVariable id: String): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }
}


@RestController
@RequestMapping("/api/stock-out-items")
@Tag(name = "StockOutItem", description = "Chiqimdagi mahsulotlar CRUD")
class StockOutItemController(private val service: StockOutItemService) {

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Yangi chiqim mahsuloti yaratish")
    fun create(@RequestBody dto: StockOutItemDTO): ResponseEntity<StockOutItemDTO> =
        ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto))

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(summary = "Bitta chiqim mahsulotini olish")
    fun getOne(@PathVariable id: String): ResponseEntity<StockOutItemDTO> =
        ResponseEntity.ok(service.getOne(id))

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(summary = "Barcha chiqim mahsulotlarini olish")
    fun getAll(): ResponseEntity<List<StockOutItemDTO>> =
        ResponseEntity.ok(service.getAll())

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Chiqim mahsulotini yangilash")
    fun update(@PathVariable id: String, @RequestBody dto: StockOutItemDTO): ResponseEntity<StockOutItemDTO> =
        ResponseEntity.ok(service.update(id, dto))

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Chiqim mahsulotini o‘chirish (soft delete)")
    fun delete(@PathVariable id: String): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }
}

@RestController
@RequestMapping("/api/product-images")
@Tag(name = "ProductImage", description = "Mahsulot rasmlari CRUD")
class ProductImageController(private val service: ProductImageService) {

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Yangi mahsulot rasm yaratish")
    fun create(@RequestBody dto: ProductImageDTO): ResponseEntity<ProductImageDTO> =
        ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto))

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE')")
    @Operation(summary = "Bitta mahsulot rasmni olish")
    fun getOne(@PathVariable id: String): ResponseEntity<ProductImageDTO> =
        ResponseEntity.ok(service.getOne(id))

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE')")
    @Operation(summary = "Barcha mahsulot rasmlarini olish")
    fun getAll(): ResponseEntity<List<ProductImageDTO>> =
        ResponseEntity.ok(service.getAll())

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mahsulot rasmni yangilash")
    fun update(@PathVariable id: String, @RequestBody dto: ProductImageDTO): ResponseEntity<ProductImageDTO> =
        ResponseEntity.ok(service.update(id, dto))

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mahsulot rasmni o‘chirish (soft delete)")
    fun delete(@PathVariable id: String): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }
}