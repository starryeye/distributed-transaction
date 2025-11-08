package dev.starryeye.monolithic.order.infrastructure;

import dev.starryeye.monolithic.order.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
