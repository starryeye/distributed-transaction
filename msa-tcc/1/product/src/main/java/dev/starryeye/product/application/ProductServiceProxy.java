package dev.starryeye.product.application;

import dev.starryeye.product.application.command.ProductReserveCommand;
import dev.starryeye.product.application.command.ReservedProductCancelCommand;
import dev.starryeye.product.application.command.ReservedProductConfirmCommand;
import dev.starryeye.product.application.result.ProductReserveResult;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductServiceProxy {

    private static final int TRY_COUNT = 3;

    private final ProductService productService;

    /**
     * 서로 다른 reservationId 를 가진 요청이지만, 동시에 같은 Product 를 업데이트하면
     * 동시성 문제(두번 갱실 분실 문제)가 생길 수 있어서 낙관적 락을 적용한다.
     */

    public ProductReserveResult tryReserve(ProductReserveCommand command) {

        int tryCount = 0;

        while (tryCount < TRY_COUNT) {
            try {
                return productService.tryReserve(command);
            } catch (ObjectOptimisticLockingFailureException e) {
                tryCount++;
            }
        }

        throw new RuntimeException("failed to reserve product.. failed to acquire optimistic lock..");
    }

    public void confirmReserve(ReservedProductConfirmCommand command) {

        int tryCount = 0;

        while (tryCount < TRY_COUNT) {
            try {
                productService.confirmReserve(command);
                return;
            } catch (ObjectOptimisticLockingFailureException e) {
                tryCount++;
            }
        }

        throw new RuntimeException("failed to reserve product.. failed to acquire optimistic lock..");
    }

    public void cancelReserve(ReservedProductCancelCommand command) {

        int tryCount = 0;

        while (tryCount < TRY_COUNT) {
            try {
                productService.cancelReserve(command);
                return;
            } catch (ObjectOptimisticLockingFailureException e) {
                tryCount++;
            }
        }

        throw new RuntimeException("failed to reserve product.. failed to acquire optimistic lock..");
    }
}
