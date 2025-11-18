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

    public void reserve() {

        if (this.status != Status.CREATED) {
            throw new RuntimeException("order can only be RESERVED during the CREATED phase, id: " + this.id + ", status: " + this.status);
        }

        this.status = Status.RESERVED;
    }

    public void cancel() {

        if (this.status != Status.RESERVED) {
            throw new RuntimeException("order can only be CANCELLED during the RESERVED phase, id: " + this.id + ", status: " + this.status);
        }

        this.status = Status.CANCELLED;
    }

    public void confirm() {

        if (this.status != Status.RESERVED && this.status != Status.PENDING) {
            throw new RuntimeException("order can only be CONFIRMED during the RESERVED or PENDING phase, id: " + this.id + ", status: " + this.status);
        }

        this.status = Status.CONFIRMED;
    }

    public void pending() {

        if (this.status != Status.RESERVED) {
            throw new RuntimeException("order can only be PENDING during the RESERVED phase, id: " + this.id + ", status: " + this.status);
        }

        this.status = Status.PENDING;
    }

    private enum Status {
        CREATED,
        RESERVED,
        CANCELLED,
        CONFIRMED,
        PENDING,
        COMPLETED;
    }
}
