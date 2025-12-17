package dasturlash.uz.warehouse_management.repository

import dasturlash.uz.warehouse_management.*
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.support.JpaEntityInformation
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository



@NoRepositoryBean
interface BaseRepository<T : BaseEntity> :
    JpaRepository<T, String>,
    JpaSpecificationExecutor<T> {

    fun findByIdAndStatusTrue(id: String): T?

    fun findAllByStatusTrue(): List<T>

    fun findAllByStatusTrue(pageable: Pageable): Page<T>

    fun disable(id: String): T?
}



class BaseRepositoryImpl<T : BaseEntity>(
    entityInformation: JpaEntityInformation<T, String>,
    entityManager: EntityManager
) : SimpleJpaRepository<T, String>(entityInformation, entityManager),
    BaseRepository<T> {

    override fun findByIdAndStatusTrue(id: String): T? =
        findByIdOrNull(id)?.takeIf { it.status }

    override fun findAllByStatusTrue(): List<T> =
        findAll().filter { it.status }

    override fun findAllByStatusTrue(pageable: Pageable): Page<T> {
        val page = findAll(pageable)

        val filtered = page.content.filter { it.status }

        return org.springframework.data.domain.PageImpl(
            filtered,
            pageable,
            filtered.size.toLong()
        )
    }


    @Transactional
    override fun disable(id: String): T? =
        findByIdOrNull(id)?.apply {
            status = false
            save(this)
        }
}



@Repository
interface WarehouseRepository : BaseRepository<Warehouse> {
    fun findByNameAndStatusTrue(name: String): Warehouse?
}



@Repository
interface EmployeeRepository : BaseRepository<Employee> {
    fun findByPhoneNumberAndStatusTrue(phoneNumber: String): Employee?
    fun findByEmployeeCodeAndStatusTrue(employeeCode: String): Employee?
    // fun findByUsername(username: String): Employee?  <-- O'chiriladi
}




@Repository
interface CategoryRepository : BaseRepository<Category> {
    fun findByNameAndStatusTrue(name: String): Category?
    fun findAllByParentAndStatusTrue(parent: Category): List<Category>
}



@Repository
interface CurrencyRepository : BaseRepository<Currency> {
    fun findByNameAndStatusTrue(name: String): Currency?
}



@Repository
interface MeasurementRepository : BaseRepository<Measurement> {
    fun findByNameAndStatusTrue(name: String): Measurement?
}



@Repository
interface ProductRepository : BaseRepository<Product> {
    fun findByUniqueNumberAndStatusTrue(uniqueNumber: String): Product?
}



@Repository
interface SupplierRepository : BaseRepository<Supplier> {
    fun findByNameAndStatusTrue(name: String): Supplier?
}



@Repository
interface StockInRepository : BaseRepository<StockIn> {
    fun findByDocumentNumberAndStatusTrue(documentNumber: String): StockIn?
    fun findByInvoiceNumberAndStatusTrue(invoiceNumber: String): StockIn?
}



@Repository
interface StockOutRepository : BaseRepository<StockOut> {
    fun findByDocumentNumberAndStatusTrue(documentNumber: String): StockOut?
    fun findByInvoiceNumberAndStatusTrue(invoiceNumber: String): StockOut?
}



@Repository
interface StockInItemRepository : BaseRepository<StockInItem> {

    fun findAllByStockIn_IdAndStatusTrue(
        stockInId: String
    ): List<StockInItem>

    fun findAllByProduct_IdAndStatusTrue(
        productId: String
    ): List<StockInItem>
}



@Repository
interface StockOutItemRepository : BaseRepository<StockOutItem> {
    fun findAllByStockOutAndStatusTrue(stockOut: StockOut): List<StockOutItem>
    fun findAllByStockInItemAndStatusTrue(stockInItem: StockInItem): List<StockOutItem>
}



@Repository
interface ProductImageRepository : BaseRepository<ProductImage> {
    fun findAllByProductAndStatusTrue(product: Product): List<ProductImage>
}
