package dasturlash.uz.warehouse_management

import jakarta.persistence.*
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDate
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
open class BaseEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: String? = null,

    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var status: Boolean = true
)

@Entity
@Table(name = "warehouses")
class Warehouse(
    @Column(nullable = false, unique = true)
    var name: String,

    @Column(nullable = false)
    var address: String
) : BaseEntity()






@Entity
@Table(name = "employees")
class Employee(
    @Column(nullable = false)
    var firstName: String,

    @Column(nullable = false)
    var lastName: String,

    @Column(nullable = false, unique = true)
    var phoneNumber: String,

    @Column(nullable = false, unique = true)
    var employeeCode: String,

    @Column(nullable = false)
    var password: String,

    @ManyToOne
    @JoinColumn(name = "warehouse_id", nullable = false)
    var warehouse: Warehouse,

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    var roles: Set<Role> = setOf(Role.EMPLOYEE)
) : BaseEntity()



@Entity
@Table(name = "categories")
class Category(
    @Column(nullable = false)
    var name: String,

    @ManyToOne
    @JoinColumn(name = "parent_id")
    var parent: Category? = null
) : BaseEntity()



@Entity
@Table(name = "currencies")
class Currency(
    @Column(nullable = false, unique = true)
    var name: String
) : BaseEntity()


@Entity
@Table(name = "measurements")
class Measurement(
    @Column(nullable = false, unique = true)
    var name: String
) : BaseEntity()




@Entity
@Table(name = "products")
class Product(
    @Column(nullable = false)
    var name: String,

    @Column(nullable = false, unique = true)
    var uniqueNumber: String,

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    var category: Category,

    @ManyToOne
    @JoinColumn(name = "measurement_id", nullable = false)
    var measurement: Measurement,

    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL], orphanRemoval = true)
    var images: MutableList<ProductImage> = mutableListOf()
) : BaseEntity()

@Entity
@Table(name = "product_images")
class ProductImage(
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    var product: Product,

    @Column(nullable = false)
    var originName: String,

    @Column(nullable = false)
    var contentType: String,

    @Column(nullable = false)
    var path: String
) : BaseEntity()


@Entity
@Table(name = "suppliers")
class Supplier(
    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var phoneNumber: String
) : BaseEntity()


@Entity
@Table(name = "stock_in")
class StockIn(
    @Column(nullable = false)
    var date: LocalDate,

    @ManyToOne
    @JoinColumn(name = "warehouse_id", nullable = false)
    var warehouse: Warehouse,

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    var supplier: Supplier,

    @ManyToOne
    @JoinColumn(name = "currency_id", nullable = false)
    var currency: Currency,

    @Column(nullable = false, unique = true)
    var documentNumber: String,

    @Column(nullable = false, unique = true)
    var invoiceNumber: String
) : BaseEntity()


@Entity
@Table(name = "stock_out")
class StockOut(
    @Column(nullable = false)
    var date: LocalDate,

    @ManyToOne
    @JoinColumn(name = "warehouse_id", nullable = false)
    var warehouse: Warehouse,

    @ManyToOne
    @JoinColumn(name = "currency_id", nullable = false)
    var currency: Currency,

    @Column(nullable = false, unique = true)
    var documentNumber: String,

    @Column(nullable = false, unique = true)
    var invoiceNumber: String
) : BaseEntity()

@Entity
@Table(name = "stock_in_items")
class StockInItem(

    @ManyToOne
    @JoinColumn(name = "stock_in_id", nullable = false)
    var stockIn: StockIn,

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    var product: Product,

    @Column(nullable = false)
    var quantity: Double,

    @Column(nullable = false)
    var inPrice: Double,

    @Column(nullable = false)
    var salePrice: Double,

    @Column
    var expireDate: LocalDate? = null
) : BaseEntity()


@Entity
@Table(name = "stock_out_items")
class StockOutItem(

    @ManyToOne
    @JoinColumn(name = "stock_out_id", nullable = false)
    var stockOut: StockOut,

    @ManyToOne
    @JoinColumn(name = "stock_in_item_id", nullable = false)
    var stockInItem: StockInItem,

    @Column(nullable = false)
    var quantity: Double,

    @Column(nullable = false)
    var outPrice: Double
) : BaseEntity()





