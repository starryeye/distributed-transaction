package dev.starryeye.point.infrastructure;

import dev.starryeye.point.domain.PointTransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PointTransactionHistoryRepository extends JpaRepository<PointTransactionHistory, Long> {

    Optional<PointTransactionHistory> findByTransactionIdAndType(String transactionId, PointTransactionHistory.Type type);

    boolean existsByTransactionIdAndType(String transactionId, PointTransactionHistory.Type type);
}
