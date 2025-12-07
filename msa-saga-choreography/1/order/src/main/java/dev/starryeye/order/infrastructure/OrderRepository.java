package dev.starryeye.order.infrastructure;

import dev.starryeye.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByIdAndCustomerId(Long id, Long customerId);
}
