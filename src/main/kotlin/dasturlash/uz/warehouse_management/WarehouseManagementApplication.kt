package dasturlash.uz.warehouse_management

import dasturlash.uz.warehouse_management.repository.BaseRepositoryImpl
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EnableJpaAuditing
@EnableJpaRepositories(
    basePackages = ["dasturlash.uz.warehouse_management.repository"],
    repositoryBaseClass = BaseRepositoryImpl::class
)
@SpringBootApplication
class WarehouseManagementApplication
fun main(args: Array<String>) {
    runApplication<WarehouseManagementApplication>(*args)
}
