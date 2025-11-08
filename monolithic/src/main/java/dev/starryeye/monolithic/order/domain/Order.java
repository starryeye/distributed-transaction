package dev.starryeye.monolithic.order.domain;

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

    // todo, status, userId

    @Builder
    private Order(Long id) {
        this.id = id;
    }

    public static Order create() {
        return Order.builder()
                .id(null)
                .build();
    }
}
