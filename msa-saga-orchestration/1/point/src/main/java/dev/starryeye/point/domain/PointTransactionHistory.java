package dev.starryeye.point.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "point_transaction_histories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointTransactionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String transactionId;

    @Enumerated(EnumType.STRING)
    private Type type;

    private Long pointId;
    private Long transactionAmount;

    @Builder
    private PointTransactionHistory(Long id, String transactionId, Type type, Long pointId, Long transactionAmount) {
        this.id = id;
        this.transactionId = transactionId;
        this.type = type;
        this.pointId = pointId;
        this.transactionAmount = transactionAmount;
    }

    public static PointTransactionHistory createUsed(String transactionId, Long pointId, Long transactionAmount) {
        return PointTransactionHistory.builder()
                .id(null)
                .transactionId(transactionId)
                .type(Type.USED)
                .pointId(pointId)
                .transactionAmount(transactionAmount)
                .build();
    }

    public PointTransactionHistory ofCancelled() {
        return PointTransactionHistory.builder()
                .id(null)
                .transactionId(this.transactionId)
                .type(Type.CANCELLED)
                .pointId(this.pointId)
                .transactionAmount(this.transactionAmount)
                .build();
    }

    public enum Type {
        USED,
        CANCELLED,
    }
}
