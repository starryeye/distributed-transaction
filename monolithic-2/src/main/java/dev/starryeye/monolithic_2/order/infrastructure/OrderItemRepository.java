package dev.starryeye.monolithic_2.order.infrastructure;

import dev.starryeye.monolithic_2.order.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
