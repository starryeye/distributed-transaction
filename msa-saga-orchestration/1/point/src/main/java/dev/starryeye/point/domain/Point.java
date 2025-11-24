package dev.starryeye.point.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "points")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long balance; // 포인트 잔액

    @Version
    private Long version;

    @Builder
    private Point(Long id, Long userId, Long balance, Long version) {
        this.id = id;
        this.userId = userId;
        this.balance = balance;
        this.version = version;
    }

    public static Point create(Long userId, Long balance) {
        return Point.builder()
                .id(null)
                .userId(userId)
                .balance(balance)
                .version(null)
                .build();
    }

    public void reduce(Long useAmount) {

        if (this.balance < useAmount) {
            throw new RuntimeException("not enough point balance..");
        }
        this.balance -= useAmount;
    }

    public void cancelReduce(Long usedAmount) {
        this.balance += usedAmount;
    }
}
