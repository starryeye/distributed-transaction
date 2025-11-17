package dev.starryeye.point.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "point_reservations")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointReservation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reservationId;

    private Long pointId;

    private Long userId;

    private Long reservedBalance;

    private ReservationStatus status;

    @Builder
    private PointReservation(Long id, String reservationId, Long pointId, Long userId, Long reservedBalance, ReservationStatus status) {
        this.id = id;
        this.reservationId = reservationId;
        this.pointId = pointId;
        this.userId = userId;
        this.reservedBalance = reservedBalance;
        this.status = status;
    }

    public static PointReservation create(String reservationId, Long pointId, Long userId, Long reservedBalance) {
        return PointReservation.builder()
                .id(null)
                .reservationId(reservationId)
                .pointId(pointId)
                .userId(userId)
                .reservedBalance(reservedBalance)
                .status(ReservationStatus.RESERVED)
                .build();
    }

    private enum ReservationStatus {
        RESERVED,
        CONFIRMED,
        CANCELLED
    }
}
