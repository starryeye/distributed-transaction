package dev.starryeye.monolithic.order.infrastructure;

import dev.starryeye.monolithic.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
