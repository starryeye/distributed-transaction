package dev.starryeye.order.infrastructure;

import dev.starryeye.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
