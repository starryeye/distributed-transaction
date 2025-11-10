package dev.starryeye.monolithic_1.order.infrastructure;

import dev.starryeye.monolithic_1.order.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
