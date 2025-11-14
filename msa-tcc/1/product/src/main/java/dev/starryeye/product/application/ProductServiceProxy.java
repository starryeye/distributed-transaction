package dev.starryeye.product.application;

import dev.starryeye.product.application.command.ProductReserveCommand;
import dev.starryeye.product.application.result.ProductReserveResult;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductServiceProxy {

    private static final int TRY_COUNT = 3;

    private final ProductService productService;

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
}
