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

    @Version // 낙관적 락 exception 처리(재시도)는 하지 않았지만, 동시성 문제 및 두번 갱실 분실 문제를 회피하기 위해 도입
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
