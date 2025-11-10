package dev.starryeye.monolithic_3.product.infrastructure;

import dev.starryeye.monolithic_3.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
