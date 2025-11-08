package dev.starryeye.monolithic.order.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "order_items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem { // 주문 상세

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private Long productId;
    private Long orderQuantity;

    @Builder
    private OrderItem(Long id, Long orderId, Long productId, Long orderQuantity) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.orderQuantity = orderQuantity;
    }

    public static OrderItem create(Long orderId, Long productId, Long orderQuantity) {
        return OrderItem.builder()
                .id(null)
                .orderId(orderId)
                .productId(productId)
                .orderQuantity(orderQuantity)
                .build();
    }
}
