package dev.starryeye.product.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "product_reservation")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductReservation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reservationId;

    private Long productId;

    private Long reservedStockQuantity;

    private Long reservedPrice;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @Builder
    private ProductReservation(Long id, String reservationId, Long productId, Long reservedStockQuantity, Long reservedPrice, ReservationStatus status) {
        this.id = id;
        this.reservationId = reservationId;
        this.productId = productId;
        this.reservedStockQuantity = reservedStockQuantity;
        this.reservedPrice = reservedPrice;
        this.status = status;
    }

    public static ProductReservation create(String reservationId, Long productId, Long reservedStockQuantity, Long reservedPrice) {
        return ProductReservation.builder()
                .id(null)
                .reservationId(reservationId)
                .productId(productId)
                .reservedStockQuantity(reservedStockQuantity)
                .reservedPrice(reservedPrice)
                .status(ReservationStatus.RESERVED)
                .build();
    }

    public boolean isConfirmed() {
        return this.status == ReservationStatus.CONFIRMED;
    }

    public boolean isReserved() {
        return this.status == ReservationStatus.RESERVED;
    }

    public boolean isCancelledOrConfirmed() {
        return this.status == ReservationStatus.CANCELLED || this.status == ReservationStatus.CONFIRMED;
    }

    public void confirm() {

        if (this.status == ReservationStatus.CANCELLED) {
            throw new IllegalStateException("product reservation has been cancelled, reservationId: " + this.reservationId + ", productId: " + this.productId);
        }

        this.status = ReservationStatus.CONFIRMED;
    }

    public void cancel() {

        if (this.status == ReservationStatus.CONFIRMED) {
            throw new IllegalStateException("product reservation has been confirm, reservationId: " + this.reservationId + ", productId: " + this.productId);
        }

        this.status = ReservationStatus.CANCELLED;
    }

    private enum ReservationStatus {
        RESERVED,
        CONFIRMED,
        CANCELLED
    }
}
