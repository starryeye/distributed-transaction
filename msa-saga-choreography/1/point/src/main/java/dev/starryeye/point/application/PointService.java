package dev.starryeye.point.application;

import dev.starryeye.point.application.command.CancelUsedPointCommand;
import dev.starryeye.point.application.command.UsePointCommand;
import dev.starryeye.point.domain.Point;
import dev.starryeye.point.domain.PointTransactionHistory;
import dev.starryeye.point.infrastructure.PointRepository;
import dev.starryeye.point.infrastructure.PointTransactionHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    private final PointTransactionHistoryRepository pointTransactionHistoryRepository;

    @Transactional
    public void use(UsePointCommand command) {

        Optional<PointTransactionHistory> transactionHistory = pointTransactionHistoryRepository.findByTransactionIdAndType(
                command.transactionId(),
                PointTransactionHistory.Type.USED
        );

        if (transactionHistory.isPresent()) {
            System.out.println("point transaction has already been processed, transactionId: " + command.transactionId());
            return;
        } // 멱등성

        Point point = pointRepository.findByUserId(command.userId())
                .orElseThrow(() -> new RuntimeException("point not found, userId: " + command.userId()));


        point.reduce(command.transactionAmount());

        PointTransactionHistory pointTransactionHistory = PointTransactionHistory.createUsed(
                command.transactionId(), point.getId(), command.transactionAmount()
        );
        pointTransactionHistoryRepository.save(pointTransactionHistory);
    }

    @Transactional
    public void cancelUse(CancelUsedPointCommand command) {

        pointTransactionHistoryRepository.findByTransactionIdAndType(
                command.transactionId(),
                PointTransactionHistory.Type.USED
        ).ifPresentOrElse(
                this::processCancellation,
                () -> System.out.println("PointTransactionHistory not found, transactionId: " + command.transactionId()) // 멱등성
        );
    }

    private void processCancellation(PointTransactionHistory usedPointTransactionHistory) {

        if (pointTransactionHistoryRepository.existsByTransactionIdAndType(
                usedPointTransactionHistory.getTransactionId(),
                PointTransactionHistory.Type.CANCELLED
        )) {
            System.out.println("point transaction has already been processed, transactionId: " + usedPointTransactionHistory.getTransactionId());
            return;
        } // 멱등성

        Point point = pointRepository.findById(usedPointTransactionHistory.getPointId())
                .orElseThrow(() -> new RuntimeException("point not found, pointId: " + usedPointTransactionHistory.getPointId()));
        point.cancelReduce(usedPointTransactionHistory.getTransactionAmount());

        PointTransactionHistory pointTransactionHistory = usedPointTransactionHistory.ofCancelled();
        pointTransactionHistoryRepository.save(pointTransactionHistory);
    }
}
