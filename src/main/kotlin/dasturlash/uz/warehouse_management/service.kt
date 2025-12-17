package dasturlash.uz.warehouse_management

import dasturlash.uz.warehouse_management.repository.*
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

// Service
interface WarehouseService {
    fun create(dto: WarehouseCreateDTO): WarehouseDTO
    fun getOne(id: String): WarehouseDTO
    fun getAll(): List<WarehouseDTO>
    fun update(id: String, dto: WarehouseUpdateDTO): WarehouseDTO
    fun delete(id: String)
}



@Service
class WarehouseServiceImpl(
    private val repository: WarehouseRepository,
    private val mapper: WarehouseMapper
) : WarehouseService {

    // POST – yangi ombor yaratish
    override fun create(dto: WarehouseCreateDTO): WarehouseDTO {
        // Agar shu nomdagi active ombor mavjud bo‘lsa xato chiqarish
        repository.findByNameAndStatusTrue(dto.name)?.let {
            throw RuntimeException("Warehouse with this name already exists")
        }
        val entity = mapper.toEntity(dto) // status = true avtomatik
        repository.save(entity)
        return mapper.toDTO(entity)
    }

    // PUT – mavjud omborni yangilash
    override fun update(id: String, dto: WarehouseUpdateDTO): WarehouseDTO {
        val entity = repository.findById(id).orElseThrow { RuntimeException("Warehouse not found") }

        entity.name = dto.name
        entity.address = dto.address

        // Agar bodyda status kelgan bo‘lsa, yangilash
        dto.status?.let { entity.status = it }

        repository.save(entity)
        return mapper.toDTO(entity)
    }

    // GET bitta ombor
    override fun getOne(id: String): WarehouseDTO {
        val entity = repository.findById(id).orElseThrow { RuntimeException("Warehouse not found") }
        if (!entity.status) throw RuntimeException("Warehouse not found") // faqat active ombor qaytariladi
        return mapper.toDTO(entity)
    }

    // GET barcha omborlar
    override fun getAll(): List<WarehouseDTO> =
        repository.findAll().map { mapper.toDTO(it) }

    // DELETE – soft delete
    override fun delete(id: String) {
        val entity = repository.findById(id).orElseThrow { RuntimeException("Warehouse not found") }
        entity.status = false
        repository.save(entity)
    }
}





interface EmployeeService {
    fun create(dto: EmployeeCreateDTO): EmployeeDTO
    fun getOne(id: String): EmployeeDTO
    fun getAll(): List<EmployeeDTO>
    fun update(id: String, dto: EmployeeUpdateDTO): EmployeeDTO
    fun delete(id: String)
}


@Service
class EmployeeServiceImpl(
    private val repository: EmployeeRepository,
    private val warehouseRepository: WarehouseRepository,
    private val mapper: EmployeeMapper,
    private val passwordEncoder: PasswordEncoder
) : EmployeeService {

    private fun validatePhone(phone: String) {
        if (!phone.matches(Regex("^\\+998\\d{9}\$"))) {
            throw RuntimeException("Phone number must start with +998 and contain 12 digits")
        }
    }

    override fun create(dto: EmployeeCreateDTO): EmployeeDTO {
        validatePhone(dto.phoneNumber)

        repository.findByPhoneNumberAndStatusTrue(dto.phoneNumber)?.let {
            throw RuntimeException("Employee with this phone already exists")
        }

        val warehouse = warehouseRepository.findByIdAndStatusTrue(dto.warehouseId)
            ?: throw RuntimeException("Warehouse not found or inactive")

        val employeeCode = "EMP-" + System.currentTimeMillis()

        val entity = mapper.toEntity(
            dto = dto,
            warehouse = warehouse,
            encodedPassword = passwordEncoder.encode(dto.password),
            employeeCode = employeeCode
        )

        repository.save(entity)
        return mapper.toDTO(entity)
    }

    override fun getOne(id: String): EmployeeDTO {
        val entity = repository.findById(id)
            .orElseThrow { RuntimeException("Employee not found") }
        return mapper.toDTO(entity)
    }

    override fun getAll(): List<EmployeeDTO> =
        repository.findAll().map { mapper.toDTO(it) }

    override fun update(id: String, dto: EmployeeUpdateDTO): EmployeeDTO {
        validatePhone(dto.phoneNumber)

        val entity = repository.findById(id)
            .orElseThrow { RuntimeException("Employee not found") }

        entity.firstName = dto.firstName
        entity.lastName = dto.lastName
        entity.phoneNumber = dto.phoneNumber
        entity.status = dto.status

        repository.save(entity)
        return mapper.toDTO(entity)
    }

    override fun delete(id: String) {
        val entity = repository.findById(id)
            .orElseThrow { RuntimeException("Employee not found") }

        entity.status = false // soft delete
        repository.save(entity)
    }
}




interface CategoryService {
    fun create(dto: CategoryCreateDTO): CategoryDTO
    fun update(id: String, dto: CategoryUpdateDTO): CategoryDTO
    fun getOne(id: String): CategoryDTO
    fun getAll(): List<CategoryDTO>
    fun delete(id: String)
}


@Service
class CategoryServiceImpl(
    private val repository: CategoryRepository,
    private val mapper: CategoryMapper
) : CategoryService {

    override fun create(dto: CategoryCreateDTO): CategoryDTO {
        val parent = dto.parentId?.let { repository.findById(it).orElse(null) }

        val entity = Category(
            name = dto.name,
            parent = parent
        ).apply {
            status = true
        }

        repository.save(entity)
        return mapper.toDTO(entity)
    }

    override fun getOne(id: String): CategoryDTO {
        val entity = repository.findById(id).orElseThrow { RuntimeException("Category not found") }
        return mapper.toDTO(entity)
    }

    override fun getAll(): List<CategoryDTO> =
        repository.findAll().map { mapper.toDTO(it) }

    override fun update(id: String, dto: CategoryUpdateDTO): CategoryDTO {
        val entity = repository.findById(id)
            .orElseThrow { RuntimeException("Category not found") }

        val parent = dto.parentId?.let { repository.findById(it).orElse(null) }

        entity.name = dto.name
        entity.parent = parent
        entity.status = dto.status

        repository.save(entity)
        return mapper.toDTO(entity)
    }

    override fun delete(id: String) {
        val entity = repository.findById(id).orElseThrow { RuntimeException("Category not found") }
        entity.status = false
        repository.save(entity)
    }
}


interface MeasurementService {
    fun create(dto: MeasurementCreateDTO): MeasurementResponseDTO
    fun getOne(id: String): MeasurementResponseDTO
    fun getAll(): List<MeasurementResponseDTO>
    fun update(id: String, dto: MeasurementUpdateDTO): MeasurementResponseDTO
    fun delete(id: String)
}


@Service
class MeasurementServiceImpl(
    private val repository: MeasurementRepository,
    private val mapper: MeasurementMapper
) : MeasurementService {

    override fun create(dto: MeasurementCreateDTO): MeasurementResponseDTO {
        repository.findByNameAndStatusTrue(dto.name)?.let {
            throw RuntimeException("Measurement with this name already exists")
        }
        val entity = mapper.toEntity(dto)
        repository.save(entity)
        return mapper.toResponseDTO(entity)
    }

    override fun getOne(id: String): MeasurementResponseDTO {
        val entity = repository.findById(id).orElseThrow { RuntimeException("Measurement not found") }
        return mapper.toResponseDTO(entity)
    }

    override fun getAll(): List<MeasurementResponseDTO> =
        repository.findAll().map { mapper.toResponseDTO(it) }

    override fun update(id: String, dto: MeasurementUpdateDTO): MeasurementResponseDTO {
        val entity = repository.findById(id).orElseThrow { RuntimeException("Measurement not found") }
        mapper.toEntity(dto, entity)
        repository.save(entity)
        return mapper.toResponseDTO(entity)
    }

    override fun delete(id: String) {
        val entity = repository.findById(id).orElseThrow { RuntimeException("Measurement not found") }
        entity.status = false
        repository.save(entity)
    }
}









interface ProductService {

    fun create(dto: ProductCreateDTO): ProductResponseDTO

    fun getOne(id: String): ProductResponseDTO

    fun getAll(): List<ProductResponseDTO>

    fun update(id: String, dto: ProductUpdateDTO): ProductResponseDTO

    fun delete(id: String)
}


@Service
class ProductServiceImpl(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val measurementRepository: MeasurementRepository,
    private val mapper: ProductMapper
) : ProductService {

    override fun create(dto: ProductCreateDTO): ProductResponseDTO {

        val category = categoryRepository.findByIdAndStatusTrue(dto.categoryId)
            ?: throw RuntimeException("Category not found or inactive")

        val measurement = measurementRepository.findByIdAndStatusTrue(dto.measurementId)
            ?: throw RuntimeException("Measurement not found or inactive")

        val uniqueNumber = "PRD-" + System.currentTimeMillis()

        val entity = mapper.toEntity(
            dto = dto,
            category = category,
            measurement = measurement,
            uniqueNumber = uniqueNumber
        )

        productRepository.save(entity)

        return mapper.toResponse(entity)
    }

    override fun getOne(id: String): ProductResponseDTO {
        val entity = productRepository.findById(id)
            .orElseThrow { RuntimeException("Product not found") }

        return mapper.toResponse(entity)
    }

    override fun getAll(): List<ProductResponseDTO> =
        productRepository.findAll()
            .map { mapper.toResponse(it) }

    override fun update(id: String, dto: ProductUpdateDTO): ProductResponseDTO {

        val entity = productRepository.findById(id)
            .orElseThrow { RuntimeException("Product not found") }

        val category = categoryRepository.findByIdAndStatusTrue(dto.categoryId)
            ?: throw RuntimeException("Category not found or inactive")

        val measurement = measurementRepository.findByIdAndStatusTrue(dto.measurementId)
            ?: throw RuntimeException("Measurement not found or inactive")

        entity.name = dto.name
        entity.category = category
        entity.measurement = measurement
        entity.status = dto.status

        productRepository.save(entity)

        return mapper.toResponse(entity)
    }

    override fun delete(id: String) {
        val entity = productRepository.findById(id)
            .orElseThrow { RuntimeException("Product not found") }

        entity.status = false
        productRepository.save(entity)
    }
}




interface SupplierService {
    fun create(dto: SupplierCreateDTO): SupplierResponseDTO
    fun getOne(id: String): SupplierResponseDTO
    fun getAll(): List<SupplierResponseDTO>
    fun update(id: String, dto: SupplierUpdateDTO): SupplierResponseDTO
    fun delete(id: String)
}


@Service
class SupplierServiceImpl(
    private val repository: SupplierRepository,
    private val mapper: SupplierMapper
) : SupplierService {

    override fun create(dto: SupplierCreateDTO): SupplierResponseDTO {
        validatePhone(dto.phoneNumber)

        repository.findByNameAndStatusTrue(dto.name)?.let {
            throw RuntimeException("Supplier already exists")
        }

        val entity = mapper.toEntity(dto)
        repository.save(entity)

        return mapper.toResponse(entity)
    }

    override fun getOne(id: String): SupplierResponseDTO {
        val entity = repository.findById(id)
            .orElseThrow { RuntimeException("Supplier not found") }

        return mapper.toResponse(entity)
    }

    override fun getAll(): List<SupplierResponseDTO> =
        repository.findAll().map { mapper.toResponse(it) }

    override fun update(id: String, dto: SupplierUpdateDTO): SupplierResponseDTO {
        validatePhone(dto.phoneNumber)

        val entity = repository.findById(id)
            .orElseThrow { RuntimeException("Supplier not found") }

        entity.name = dto.name
        entity.phoneNumber = dto.phoneNumber
        entity.status = dto.status

        repository.save(entity)
        return mapper.toResponse(entity)
    }

    override fun delete(id: String) {
        val entity = repository.findById(id)
            .orElseThrow { RuntimeException("Supplier not found") }

        entity.status = false
        repository.save(entity)
    }

    private fun validatePhone(phone: String) {
        if (!phone.matches(Regex("^\\+998\\d{9}\$"))) {
            throw RuntimeException("Phone number must start with +998 and contain 12 digits")
        }
    }
}



interface StockInService {
    fun create(dto: StockInCreateDTO): StockInResponseDTO
    fun getOne(id: String): StockInResponseDTO
    fun getAll(): List<StockInResponseDTO>
    fun update(id: String, dto: StockInUpdateDTO): StockInResponseDTO
    fun delete(id: String)
}

@Service
class StockInServiceImpl(
    private val repository: StockInRepository,
    private val warehouseRepository: WarehouseRepository,
    private val supplierRepository: SupplierRepository,
    private val currencyRepository: CurrencyRepository,
    private val mapper: StockInMapper
) : StockInService {

    override fun create(dto: StockInCreateDTO): StockInResponseDTO {
        val warehouse = warehouseRepository.findByIdAndStatusTrue(dto.warehouseId)
            ?: throw RuntimeException("Warehouse not found")
        val supplier = supplierRepository.findByIdAndStatusTrue(dto.supplierId)
            ?: throw RuntimeException("Supplier not found")
        val currency = currencyRepository.findByIdAndStatusTrue(dto.currencyId)
            ?: throw RuntimeException("Currency not found")

        val entity = mapper.toEntity(dto, warehouse, supplier, currency)
        repository.save(entity)
        return mapper.toResponseDTO(entity)
    }

    override fun getOne(id: String): StockInResponseDTO {
        val entity = repository.findById(id).orElseThrow { RuntimeException("StockIn not found") }
        return mapper.toResponseDTO(entity)
    }

    override fun getAll(): List<StockInResponseDTO> =
        repository.findAll().map { mapper.toResponseDTO(it) }

    override fun update(id: String, dto: StockInUpdateDTO): StockInResponseDTO {
        val entity = repository.findById(id).orElseThrow { RuntimeException("StockIn not found") }
        mapper.updateEntity(entity, dto)
        repository.save(entity)
        return mapper.toResponseDTO(entity)
    }

    override fun delete(id: String) {
        val entity = repository.findById(id).orElseThrow { RuntimeException("StockIn not found") }
        entity.status = false
        repository.save(entity)
    }
}



interface StockOutService {
    fun create(dto: StockOutCreateDTO): StockOutResponseDTO
    fun getOne(id: String): StockOutResponseDTO
    fun getAll(): List<StockOutResponseDTO>
    fun update(id: String, dto: StockOutUpdateDTO): StockOutResponseDTO
    fun delete(id: String)
}

@Service
class StockOutServiceImpl(
    private val repository: StockOutRepository,
    private val warehouseRepository: WarehouseRepository,
    private val currencyRepository: CurrencyRepository,
    private val mapper: StockOutMapper
) : StockOutService {

    override fun create(dto: StockOutCreateDTO): StockOutResponseDTO {
        val warehouse = warehouseRepository.findByIdAndStatusTrue(dto.warehouseId)
            ?: throw RuntimeException("Warehouse not found")
        val currency = currencyRepository.findByIdAndStatusTrue(dto.currencyId)
            ?: throw RuntimeException("Currency not found")
        val entity = mapper.toEntity(dto, warehouse, currency)
        repository.save(entity)
        return mapper.toResponseDTO(entity)
    }

    override fun getOne(id: String): StockOutResponseDTO {
        val entity = repository.findById(id).orElseThrow { RuntimeException("StockOut not found") }
        return mapper.toResponseDTO(entity)
    }

    override fun getAll(): List<StockOutResponseDTO> =
        repository.findAll().map { mapper.toResponseDTO(it) }

    override fun update(id: String, dto: StockOutUpdateDTO): StockOutResponseDTO {
        val entity = repository.findById(id).orElseThrow { RuntimeException("StockOut not found") }
        mapper.updateEntity(entity, dto)
        repository.save(entity)
        return mapper.toResponseDTO(entity)
    }

    override fun delete(id: String) {
        val entity = repository.findById(id).orElseThrow { RuntimeException("StockOut not found") }
        entity.status = false
        repository.save(entity)
    }
}



interface StockInItemService {
    fun create(dto: StockInItemCreateDTO): StockInItemResponseDTO
    fun getOne(id: String): StockInItemResponseDTO
    fun getAll(): List<StockInItemResponseDTO>
    fun update(id: String, dto: StockInItemUpdateDTO): StockInItemResponseDTO
    fun delete(id: String)
}

@Service
class StockInItemServiceImpl(
    private val repository: StockInItemRepository,
    private val stockInRepository: StockInRepository,
    private val productRepository: ProductRepository,
    private val mapper: StockInItemMapper
) : StockInItemService {

    override fun create(dto: StockInItemCreateDTO): StockInItemResponseDTO {
        val stockIn = stockInRepository.findByIdAndStatusTrue(dto.stockInId)
            ?: throw RuntimeException("StockIn not found")
        val product = productRepository.findByIdAndStatusTrue(dto.productId)
            ?: throw RuntimeException("Product not found")
        val entity = mapper.toEntity(dto, stockIn, product)
        repository.save(entity)
        return mapper.toResponseDTO(entity)
    }

    override fun getOne(id: String): StockInItemResponseDTO {
        val entity = repository.findById(id).orElseThrow { RuntimeException("StockInItem not found") }
        return mapper.toResponseDTO(entity)
    }

    override fun getAll(): List<StockInItemResponseDTO> =
        repository.findAll().map { mapper.toResponseDTO(it) } // true/false status hammasi

    override fun update(id: String, dto: StockInItemUpdateDTO): StockInItemResponseDTO {
        val entity = repository.findById(id).orElseThrow { RuntimeException("StockInItem not found") }
        mapper.updateEntity(entity, dto)
        repository.save(entity)
        return mapper.toResponseDTO(entity)
    }

    override fun delete(id: String) {
        val entity = repository.findById(id).orElseThrow { RuntimeException("StockInItem not found") }
        entity.status = false
        repository.save(entity)
    }
}



interface StockOutItemService {
    fun create(dto: StockOutItemCreateDTO): StockOutItemResponseDTO
    fun getOne(id: String): StockOutItemResponseDTO
    fun getAll(): List<StockOutItemResponseDTO>
    fun update(id: String, dto: StockOutItemUpdateDTO): StockOutItemResponseDTO
    fun delete(id: String)
}

@Service
class StockOutItemServiceImpl(
    private val repository: StockOutItemRepository,
    private val stockOutRepository: StockOutRepository,
    private val stockInItemRepository: StockInItemRepository,
    private val mapper: StockOutItemMapper
) : StockOutItemService {

    override fun create(dto: StockOutItemCreateDTO): StockOutItemResponseDTO {
        val stockOut = stockOutRepository.findByIdAndStatusTrue(dto.stockOutId)
            ?: throw RuntimeException("StockOut not found")
        val stockInItem = stockInItemRepository.findByIdAndStatusTrue(dto.stockInItemId)
            ?: throw RuntimeException("StockInItem not found")
        val entity = mapper.toEntity(dto, stockOut, stockInItem)
        repository.save(entity)
        return mapper.toResponseDTO(entity)
    }

    override fun getOne(id: String): StockOutItemResponseDTO {
        val entity = repository.findById(id).orElseThrow { RuntimeException("StockOutItem not found") }
        return mapper.toResponseDTO(entity)
    }

    override fun getAll(): List<StockOutItemResponseDTO> =
        repository.findAll().map { mapper.toResponseDTO(it) } // true/false hammasi

    override fun update(id: String, dto: StockOutItemUpdateDTO): StockOutItemResponseDTO {
        val entity = repository.findById(id).orElseThrow { RuntimeException("StockOutItem not found") }
        mapper.updateEntity(entity, dto)
        repository.save(entity)
        return mapper.toResponseDTO(entity)
    }

    override fun delete(id: String) {
        val entity = repository.findById(id).orElseThrow { RuntimeException("StockOutItem not found") }
        entity.status = false
        repository.save(entity)
    }
}


interface ProductImageService {
    fun create(dto: ProductImageCreateDTO): ProductImageResponseDTO
    fun getOne(id: String): ProductImageResponseDTO
    fun getAll(): List<ProductImageResponseDTO>
    fun update(id: String, dto: ProductImageUpdateDTO): ProductImageResponseDTO
    fun delete(id: String)
}

@Service
class ProductImageServiceImpl(
    private val repository: ProductImageRepository,
    private val productRepository: ProductRepository,
    private val mapper: ProductImageMapper
) : ProductImageService {

    override fun create(dto: ProductImageCreateDTO): ProductImageResponseDTO {
        val product = productRepository.findByIdAndStatusTrue(dto.productId)
            ?: throw RuntimeException("Product not found")
        val entity = mapper.toEntity(dto, product)
        repository.save(entity)
        return mapper.toResponseDTO(entity)
    }

    override fun getOne(id: String): ProductImageResponseDTO {
        val entity = repository.findById(id)
            .orElseThrow { RuntimeException("ProductImage not found") }
        return mapper.toResponseDTO(entity)
    }

    override fun getAll(): List<ProductImageResponseDTO> =
        repository.findAll().map { mapper.toResponseDTO(it) }

    override fun update(id: String, dto: ProductImageUpdateDTO): ProductImageResponseDTO {
        val entity = repository.findById(id)
            .orElseThrow { RuntimeException("ProductImage not found") }
        mapper.updateEntity(entity, dto)
        repository.save(entity)
        return mapper.toResponseDTO(entity)
    }

    override fun delete(id: String) {
        val entity = repository.findById(id)
            .orElseThrow { RuntimeException("ProductImage not found") }
        entity.status = false
        repository.save(entity)
    }
}




