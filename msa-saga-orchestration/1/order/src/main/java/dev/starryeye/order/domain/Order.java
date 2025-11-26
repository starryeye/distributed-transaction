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

    public void request() {

        if (this.status != Status.CREATED) {
            throw new RuntimeException("order can only be REQUESTED during the CREATED phase, id: " + this.id + ", status: " + this.status);
        }

        this.status = Status.REQUESTED;
    }

    public void fail() {

        if (this.status != Status.REQUESTED) {
            throw new RuntimeException("order can only be FAILED during the REQUESTED phase, id: " + this.id + ", status: " + this.status);
        }

        this.status = Status.FAILED;
    }

    private enum Status {
        CREATED,
        REQUESTED,
        COMPLETED,
        FAILED,
        ;
    }
}
