package dev.starryeye.monolithic_1.order.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;

    @Builder
    private Order(Long id, Long customerId) {
        this.id = id;
        this.customerId = customerId;
    }

    public static Order create(Long customerId) {
        return Order.builder()
                .id(null)
                .customerId(customerId)
                .build();
    }
}
