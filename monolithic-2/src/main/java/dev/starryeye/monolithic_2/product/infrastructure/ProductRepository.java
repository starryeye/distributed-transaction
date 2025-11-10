package dev.starryeye.monolithic_2.product.infrastructure;

import dev.starryeye.monolithic_2.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
