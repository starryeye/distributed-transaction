package dev.starryeye.product.infrastructure;

import dev.starryeye.product.domain.ProductBoughtHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductBoughtHistoryRepository extends JpaRepository<ProductBoughtHistory, Long> {

    List<ProductBoughtHistory> findAllByBoughtIdAndType(String boughtId, ProductBoughtHistory.Type type);
}
