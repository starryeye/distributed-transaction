package dev.starryeye.monolithic_2.order.infrastructure;

import dev.starryeye.monolithic_2.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
