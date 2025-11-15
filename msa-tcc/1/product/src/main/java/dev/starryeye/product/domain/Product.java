package dev.starryeye.product.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "products")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long stockQuantity; // 재고

    private Long price; // 가격

    private Long reservedStockQuantity;

    @Version
    private Long version;

    @Builder
    private Product(Long id, Long stockQuantity, Long price, Long reservedStockQuantity, Long version) {
        this.id = id;
        this.stockQuantity = stockQuantity;
        this.price = price;
        this.reservedStockQuantity = reservedStockQuantity;
        this.version = version;
    }

    public static Product create(Long stockQuantity, Long price) {
        return Product.builder()
                .id(null)
                .stockQuantity(stockQuantity)
                .price(price)
                .reservedStockQuantity(0L)
                .version(null)
                .build();
    }

    public Long getPriceFor(Long quantity) {
        return this.price * quantity;
    }

    public void reduceStock(Long orderQuantity) {

        if (this.stockQuantity < orderQuantity) {
            throw new RuntimeException("orderQuantity exceeds stock quantity, productId: " + this.id + ", orderQuantity: " + orderQuantity + ", stockQuantity: " + stockQuantity);
        }

        this.stockQuantity -= orderQuantity;
    }

    public void reserveStock(Long requestReserveStockQuantity) {
        // 상품 재고 예약

        long reservableStockQuantity = this.stockQuantity - this.reservedStockQuantity;

        if (reservableStockQuantity < requestReserveStockQuantity) { // 예약 가능 수량 체크
            throw new RuntimeException("not enough available reservations, productId: " + this.id + ", stockQuantity: " + reservableStockQuantity + ", stockQuantity: " + requestReserveStockQuantity);
        }

        this.reservedStockQuantity += requestReserveStockQuantity;
    }

    public void confirmReservedStock(Long requestConfirmStockQuantity) {
        // 상품 재고 예약 확정 -> 재고 및 예약 재고 감소

        if (this.stockQuantity < requestConfirmStockQuantity) {
            throw new RuntimeException("requestConfirmStockQuantity exceeds stock quantity, productId: " + this.id + ", requestConfirmStockQuantity: " + requestConfirmStockQuantity + ", stockQuantity: " + stockQuantity);
        }

        if (this.reservedStockQuantity < requestConfirmStockQuantity) {
            throw new RuntimeException("requestConfirmStockQuantity exceeds reservedStockQuantity, productId: " + this.id + ", requestConfirmStockQuantity: " + requestConfirmStockQuantity + ", reservedStockQuantity: " + reservedStockQuantity);
        }

        this.stockQuantity -= requestConfirmStockQuantity;
        this.reservedStockQuantity -= requestConfirmStockQuantity;
    }
}
