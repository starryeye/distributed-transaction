package dev.starryeye.product.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "product_bought_histories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductBoughtHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String boughtId;

    @Enumerated(EnumType.STRING)
    private Type type;

    private Long productId;
    private Long boughtStockQuantity;
    private Long boughtPrice; // stockQuantity * price

    @Builder
    private ProductBoughtHistory(Long id, String boughtId, Type type, Long productId, Long boughtStockQuantity, Long boughtPrice) {
        this.id = id;
        this.boughtId = boughtId;
        this.type = type;
        this.productId = productId;
        this.boughtStockQuantity = boughtStockQuantity;
        this.boughtPrice = boughtPrice;
    }

    public static ProductBoughtHistory createBought(String boughtId, Long productId, Long boughtStockQuantity, Long boughtPrice) {
        return ProductBoughtHistory.builder()
                .id(null)
                .boughtId(boughtId)
                .type(Type.BOUGHT)
                .productId(productId)
                .boughtStockQuantity(boughtStockQuantity)
                .boughtPrice(boughtPrice)
                .build();
    }

    public ProductBoughtHistory ofCancelled() {
        return ProductBoughtHistory.builder()
                .id(null)
                .boughtId(boughtId)
                .type(Type.CANCELLED)
                .productId(productId)
                .boughtStockQuantity(boughtStockQuantity)
                .boughtPrice(boughtPrice)
                .build();
    }

    public enum Type {
        BOUGHT,
        CANCELLED,
    }
}
