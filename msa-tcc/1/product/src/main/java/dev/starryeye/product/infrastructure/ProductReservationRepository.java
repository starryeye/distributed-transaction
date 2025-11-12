package dev.starryeye.product.infrastructure;

import dev.starryeye.product.domain.ProductReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductReservationRepository extends JpaRepository<ProductReservation, Long> {

    List<ProductReservation> findAllByReservationId(String reservationId);
}
