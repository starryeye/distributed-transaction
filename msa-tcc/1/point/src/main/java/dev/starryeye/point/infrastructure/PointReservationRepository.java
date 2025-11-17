package dev.starryeye.point.infrastructure;

import dev.starryeye.point.domain.PointReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PointReservationRepository extends JpaRepository<PointReservation, Long> {

    Optional<PointReservation> findByReservationId(String reservationId);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
            "FROM PointReservation p WHERE p.reservationId = :reservationId")
    boolean existsByReservationId(@Param("reservationId") String reservationId);
}
