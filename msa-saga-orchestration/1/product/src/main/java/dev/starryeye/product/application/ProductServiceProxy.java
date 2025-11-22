package dev.starryeye.product.application;

import dev.starryeye.product.application.command.BuyProductCommand;
import dev.starryeye.product.application.command.CancelBoughtProductCommand;
import dev.starryeye.product.application.result.BuyProductResult;
import dev.starryeye.product.application.result.CancelBoughtProductResult;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductServiceProxy {

    private static final int TRY_COUNT = 3;

    private final ProductService target;

    /**
     * 서로 다른 boughtId 를 가진 요청이지만, 동시에 같은 Product 를 업데이트하면
     * 동시성 문제(두번 갱실 분실 문제)가 생길 수 있어서 낙관적 락을 적용한다.
     */

    public BuyProductResult buy(BuyProductCommand command) {

        int tryCount = 0;

        while (tryCount < TRY_COUNT) {
            try {
                return target.buy(command);
            } catch (ObjectOptimisticLockingFailureException e) {
                tryCount++;
            }
        }

        throw new RuntimeException("failed to reserve product.. failed to acquire optimistic lock..");
    }

    public CancelBoughtProductResult cancelBuying(CancelBoughtProductCommand command) {

        int tryCount = 0;

        while (tryCount < TRY_COUNT) {
            try {
                return target.cancelBuying(command);
            } catch (ObjectOptimisticLockingFailureException e) {
                tryCount++;
            }
        }

        throw new RuntimeException("failed to reserve product.. failed to acquire optimistic lock..");
    }
}
