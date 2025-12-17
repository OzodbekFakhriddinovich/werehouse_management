package dasturlash.uz.warehouse_management

import org.springframework.stereotype.Component

@Component
class WarehouseMapper {

    // POST: DTO -> Entity (status avtomatik true)
    fun toEntity(dto: WarehouseCreateDTO): Warehouse =
        Warehouse(name = dto.name, address = dto.address).apply {
            status = true
        }

    // Entity -> DTO
    fun toDTO(entity: Warehouse): WarehouseDTO =
        WarehouseDTO(
            id = entity.id!!,
            name = entity.name,
            address = entity.address,
            status = entity.status
        )
}





@Component
class EmployeeMapper {

    fun toEntity(
        dto: EmployeeCreateDTO,
        warehouse: Warehouse,
        encodedPassword: String,
        employeeCode: String
    ): Employee =
        Employee(
            firstName = dto.firstName,
            lastName = dto.lastName,
            phoneNumber = dto.phoneNumber,
            employeeCode = employeeCode,
            password = encodedPassword,
            warehouse = warehouse
        ).apply {
            status = true // default true
        }

    fun toDTO(entity: Employee): EmployeeDTO =
        EmployeeDTO(
            id = entity.id,
            firstName = entity.firstName,
            lastName = entity.lastName,
            phoneNumber = entity.phoneNumber,
            employeeCode = entity.employeeCode,
            warehouseId = entity.warehouse.id!!,
            status = entity.status
        )
}






@Component
class CategoryMapper {

    fun toEntity(dto: CategoryCreateDTO, parent: Category?): Category =
        Category(
            name = dto.name,
            parent = parent
        ).apply {
            status = true
        }

    fun updateEntity(
        dto: CategoryUpdateDTO,
        parent: Category?,
        entity: Category
    ): Category {
        entity.name = dto.name
        entity.parent = parent
        entity.status = dto.status
        return entity
    }

    fun toDTO(entity: Category): CategoryDTO =
        CategoryDTO(
            id = entity.id!!,
            name = entity.name,
            parentId = entity.parent?.id,
            status = entity.status
        )
}






@Component
class CurrencyMapper {

    fun toEntity(dto: CurrencyDTO): Currency =
        Currency(name = dto.name).apply {
            status = dto.status
            id = dto.id
        }

    fun toDTO(entity: Currency): CurrencyDTO =
        CurrencyDTO(
            id = entity.id,
            name = entity.name,
            status = entity.status
        )
}


@Component
class MeasurementMapper {

    fun toEntity(dto: MeasurementCreateDTO): Measurement =
        Measurement(name = dto.name).apply {
            status = true // POST default active
        }

    fun toEntity(dto: MeasurementUpdateDTO, entity: Measurement): Measurement {
        entity.name = dto.name
        entity.status = dto.status
        return entity
    }

    fun toResponseDTO(entity: Measurement): MeasurementResponseDTO =
        MeasurementResponseDTO(
            id = entity.id!!,
            name = entity.name,
            status = entity.status
        )
}





@Component
class ProductMapper {

    fun toEntity(
        dto: ProductCreateDTO,
        category: Category,
        measurement: Measurement,
        uniqueNumber: String
    ): Product =
        Product(
            name = dto.name,
            uniqueNumber = uniqueNumber,
            category = category,
            measurement = measurement
        )
    // status = true avtomatik (BaseEntity)

    fun toResponse(entity: Product): ProductResponseDTO =
        ProductResponseDTO(
            id = entity.id,
            name = entity.name,
            uniqueNumber = entity.uniqueNumber,
            categoryId = entity.category.id!!,
            measurementId = entity.measurement.id!!,
            status = entity.status
        )
}





@Component
class SupplierMapper {

    fun toEntity(dto: SupplierCreateDTO): Supplier =
        Supplier(
            name = dto.name,
            phoneNumber = dto.phoneNumber
        )
    // status avtomatik true (BaseEntity dan)

    fun toResponse(entity: Supplier): SupplierResponseDTO =
        SupplierResponseDTO(
            id = entity.id!!,
            name = entity.name,
            phoneNumber = entity.phoneNumber,
            status = entity.status
        )
}



@Component
class StockInMapper {

    // POST uchun DTO -> Entity
    fun toEntity(dto: StockInCreateDTO, warehouse: Warehouse, supplier: Supplier, currency: Currency): StockIn =
        StockIn(
            date = dto.date,
            warehouse = warehouse,
            supplier = supplier,
            currency = currency,
            documentNumber = dto.documentNumber,
            invoiceNumber = dto.invoiceNumber
        ).apply {
            status = true // POST da default true
        }

    // PUT uchun update
    fun updateEntity(entity: StockIn, dto: StockInUpdateDTO) {
        entity.date = dto.date
        entity.warehouse = entity.warehouse
        entity.supplier = entity.supplier
        entity.currency = entity.currency
        entity.documentNumber = dto.documentNumber
        entity.invoiceNumber = dto.invoiceNumber
        dto.status?.let { entity.status = it }
    }

    fun toResponseDTO(entity: StockIn): StockInResponseDTO =
        StockInResponseDTO(
            id = entity.id!!,
            date = entity.date,
            warehouseId = entity.warehouse.id!!,
            supplierId = entity.supplier.id!!,
            currencyId = entity.currency.id!!,
            documentNumber = entity.documentNumber,
            invoiceNumber = entity.invoiceNumber,
            status = entity.status
        )
}



@Component
class StockOutMapper {

    fun toEntity(dto: StockOutCreateDTO, warehouse: Warehouse, currency: Currency): StockOut =
        StockOut(
            date = dto.date,
            warehouse = warehouse,
            currency = currency,
            documentNumber = dto.documentNumber,
            invoiceNumber = dto.invoiceNumber
        ).apply { status = true } // POST default true

    fun updateEntity(entity: StockOut, dto: StockOutUpdateDTO) {
        entity.date = dto.date
        entity.warehouse = entity.warehouse
        entity.currency = entity.currency
        entity.documentNumber = dto.documentNumber
        entity.invoiceNumber = dto.invoiceNumber
        dto.status?.let { entity.status = it }
    }

    fun toResponseDTO(entity: StockOut): StockOutResponseDTO =
        StockOutResponseDTO(
            id = entity.id!!,
            date = entity.date,
            warehouseId = entity.warehouse.id!!,
            currencyId = entity.currency.id!!,
            documentNumber = entity.documentNumber,
            invoiceNumber = entity.invoiceNumber,
            status = entity.status
        )
}



@Component
class StockInItemMapper {

    fun toEntity(dto: StockInItemCreateDTO, stockIn: StockIn, product: Product): StockInItem =
        StockInItem(
            stockIn = stockIn,
            product = product,
            quantity = dto.quantity,
            inPrice = dto.inPrice,
            salePrice = dto.salePrice,
            expireDate = dto.expireDate
        ).apply { status = true }

    fun updateEntity(entity: StockInItem, dto: StockInItemUpdateDTO) {
        entity.stockIn = entity.stockIn
        entity.product = entity.product
        entity.quantity = dto.quantity
        entity.inPrice = dto.inPrice
        entity.salePrice = dto.salePrice
        entity.expireDate = dto.expireDate
        dto.status?.let { entity.status = it } // PUT da optional
    }

    fun toResponseDTO(entity: StockInItem): StockInItemResponseDTO =
        StockInItemResponseDTO(
            id = entity.id!!,
            stockInId = entity.stockIn.id!!,
            productId = entity.product.id!!,
            quantity = entity.quantity,
            inPrice = entity.inPrice,
            salePrice = entity.salePrice,
            expireDate = entity.expireDate,
            status = entity.status
        )
}



@Component
class StockOutItemMapper {

    // POST uchun DTO -> Entity
    fun toEntity(dto: StockOutItemCreateDTO, stockOut: StockOut, stockInItem: StockInItem): StockOutItem =
        StockOutItem(
            stockOut = stockOut,
            stockInItem = stockInItem,
            quantity = dto.quantity,
            outPrice = dto.outPrice
        ).apply { status = true } // default true POST da

    // PUT uchun update
    fun updateEntity(entity: StockOutItem, dto: StockOutItemUpdateDTO) {
        entity.stockOut = entity.stockOut
        entity.stockInItem = entity.stockInItem
        entity.quantity = dto.quantity
        entity.outPrice = dto.outPrice
        dto.status?.let { entity.status = it }
    }

    // Entity -> Response DTO
    fun toResponseDTO(entity: StockOutItem): StockOutItemResponseDTO =
        StockOutItemResponseDTO(
            id = entity.id!!,
            stockOutId = entity.stockOut.id!!,
            stockInItemId = entity.stockInItem.id!!,
            quantity = entity.quantity,
            outPrice = entity.outPrice,
            status = entity.status
        )
}



@Component
class ProductImageMapper {

    fun toEntity(dto: ProductImageCreateDTO, product: Product): ProductImage =
        ProductImage(
            product = product,
            originName = dto.originName,
            contentType = dto.contentType,
            path = dto.path
        ).apply {
            status = true // POST da default true
        }

    fun updateEntity(entity: ProductImage, dto: ProductImageUpdateDTO) {
        entity.originName = dto.originName
        entity.contentType = dto.contentType
        entity.path = dto.path
        dto.status?.let { entity.status = it } // PUT da status optional
    }

    fun toResponseDTO(entity: ProductImage): ProductImageResponseDTO =
        ProductImageResponseDTO(
            id = entity.id!!,
            productId = entity.product.id!!,
            originName = entity.originName,
            contentType = entity.contentType,
            path = entity.path,
            status = entity.status
        )
}





