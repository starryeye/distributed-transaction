package dev.starryeye.monolithic_1.product.infrastructure;

import dev.starryeye.monolithic_1.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
