package dev.starryeye.order.domain;

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

    @Enumerated(EnumType.STRING)
    private Status status;

    @Builder
    private Order(Long id, Long customerId, Status status) {
        this.id = id;
        this.customerId = customerId;
        this.status = status;
    }

    public static Order create(Long customerId) {
        return Order.builder()
                .id(null)
                .customerId(customerId)
                .status(Status.CREATED)
                .build();
    }

    public boolean isComplete() {
        return status == Status.COMPLETED;
    }

    public void complete() {
        status = Status.COMPLETED;
    }

    private enum Status {
        CREATED,
        COMPLETED;
    }
}
