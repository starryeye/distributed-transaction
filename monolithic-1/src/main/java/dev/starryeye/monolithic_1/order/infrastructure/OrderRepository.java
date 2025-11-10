package dev.starryeye.monolithic_1.order.infrastructure;

import dev.starryeye.monolithic_1.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
