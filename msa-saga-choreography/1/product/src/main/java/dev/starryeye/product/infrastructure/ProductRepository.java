package dev.starryeye.product.infrastructure;

import dev.starryeye.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
