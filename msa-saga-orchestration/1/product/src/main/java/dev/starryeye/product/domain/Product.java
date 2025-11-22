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

    @Builder
    private Product(Long id, Long stockQuantity, Long price) {
        this.id = id;
        this.stockQuantity = stockQuantity;
        this.price = price;
    }

    public static Product create(Long stockQuantity, Long price) {
        return Product.builder()
                .id(null)
                .stockQuantity(stockQuantity)
                .price(price)
                .build();
    }

    public Long getPriceFor(Long orderQuantity) {
        return this.price * orderQuantity;
    }

    public void reduceStock(Long orderQuantity) {

        if (stockQuantity < orderQuantity) {
            throw new RuntimeException("stock quantity exceeds stock quantity, productId: " + this.id + ", orderQuantity: " + orderQuantity + ", stockQuantity: " + stockQuantity);
        }

        this.stockQuantity -= orderQuantity;
    }

    public void cancelReduceStock(Long orderQuantity) {

        this.stockQuantity += orderQuantity;
    }
}
