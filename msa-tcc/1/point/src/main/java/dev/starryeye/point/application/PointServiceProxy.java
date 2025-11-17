package dev.starryeye.point.application;

import dev.starryeye.point.application.command.PointReserveCommand;
import dev.starryeye.point.application.command.ReservedPointConfirmCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PointServiceProxy {

    private static final int TRY_COUNT = 3;

    private final PointService target;

    /**
     * 서로 다른 reservationId 를 가진 요청이지만, 동시에 같은 Point 를 업데이트하면
     * 동시성 문제(두번 갱실 분실 문제)가 생길 수 있어서 낙관적 락을 적용한다.
     */

    public void tryReserve(PointReserveCommand command) {

        int tryCount = 0;

        while (tryCount < TRY_COUNT) {
            try {
                target.tryReserve(command);
                return;
            } catch (ObjectOptimisticLockingFailureException e) {
                tryCount++;
            }
        }

        throw new RuntimeException("failed to reserve product.. failed to acquire optimistic lock..");
    }

    public void confirmReserve(ReservedPointConfirmCommand command) {

        int tryCount = 0;

        while (tryCount < TRY_COUNT) {
            try {
                target.confirmReserve(command);
                return;
            } catch (ObjectOptimisticLockingFailureException e) {
                tryCount++;
            }
        }

        throw new RuntimeException("failed to reserve product.. failed to acquire optimistic lock..");
    }
}
