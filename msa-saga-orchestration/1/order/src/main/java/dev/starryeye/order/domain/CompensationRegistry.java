package dev.starryeye.order.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "compensation_registries")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CompensationRegistry {

    /**
     * 보상트랜잭션 기록
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    private Status status;

    @Builder
    private CompensationRegistry(Long id, Long orderId, Status status) {
        this.id = id;
        this.orderId = orderId;
        this.status = status;
    }

    public static CompensationRegistry createPending(Long orderId) {
        return CompensationRegistry.builder()
                .id(null)
                .orderId(orderId)
                .status(Status.PENDING)
                .build();
    }

    public enum Status {
        PENDING,
        COMPLETED,
    }
}
