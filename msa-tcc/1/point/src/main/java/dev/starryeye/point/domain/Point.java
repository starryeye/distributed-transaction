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

    private Long reservedBalance;

    @Version
    private Long version;

    @Builder
    private Point(Long id, Long userId, Long balance, Long reservedBalance, Long version) {
        this.id = id;
        this.userId = userId;
        this.balance = balance;
        this.reservedBalance = reservedBalance;
        this.version = version;
    }

    public static Point create(Long userId, Long balance, Long reservedBalance) {
        return Point.builder()
                .id(null)
                .userId(userId)
                .balance(balance)
                .reservedBalance(reservedBalance)
                .version(null)
                .build();
    }

    public void reduce(Long useBalance) {

        if (this.balance < useBalance) {
            throw new RuntimeException("not enough point balance, pointId: " + this.id + ", balance: " + this.balance + ", useBalance: " + useBalance);
        }
        this.balance -= useBalance;
    }

    public void reserveBalance(Long requestReserveBalance) {

        long reservableBalance = this.balance - this.reservedBalance;

        if (reservableBalance < requestReserveBalance) {
            throw new RuntimeException("not enough point balance, pointId: " + this.id + ", balance: " + this.balance + ", reservedBalance: " + this.reservedBalance + ", requestReserveBalance: " + requestReserveBalance);
        }

        this.reservedBalance += requestReserveBalance;
    }

    public void confirmReservedBalance(Long requestConfirmBalance) {

        if (this.balance < requestConfirmBalance) {
            throw new RuntimeException("not enough point balance, pointId: " + this.id + ", balance: " + this.balance + ", reservedBalance: " + reservedBalance + ", requestConfirmBalance: " + requestConfirmBalance);
        }

        if (this.reservedBalance < requestConfirmBalance) {
            throw new RuntimeException("not enough point reservedBalance, pointId: " + this.id + ", balance: " + this.balance + ", reservedBalance: " + reservedBalance + ", requestConfirmBalance: " + requestConfirmBalance);
        }

        this.balance -= requestConfirmBalance;
        this.reservedBalance -= requestConfirmBalance;
    }

    public void cancelReservedBalance(Long requestCancelBalance) {

        if (this.reservedBalance < requestCancelBalance) {
            throw new RuntimeException("not enough point reservedBalance, pointId: " + this.id + ", balance: " + this.balance + ", reservedBalance: " + reservedBalance + ", requestConfirmBalance: " + requestCancelBalance);
        }

        this.reservedBalance -= requestCancelBalance;
    }
}
