package dev.starryeye.monolithic.product.infrastructure;

import dev.starryeye.monolithic.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
