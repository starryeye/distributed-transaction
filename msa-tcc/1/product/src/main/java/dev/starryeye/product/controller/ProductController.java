package dev.starryeye.product.controller;

import dev.starryeye.product.application.ProductService;
import dev.starryeye.product.application.ProductServiceProxy;
import dev.starryeye.product.application.result.ProductReserveResult;
import dev.starryeye.product.common.infrastructure.RedisDistributedLock;
import dev.starryeye.product.controller.request.ProductReserveRequest;
import dev.starryeye.product.controller.response.ProductReserveResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductServiceProxy productServiceProxy;

    private final RedisDistributedLock lock;

    @PostMapping("/product/reserve")
    public ProductReserveResponse reserve(
            @RequestBody ProductReserveRequest request
    ) {
        /**
         * 모종의 이유로 동시에 동일한 reservationId 의 요청이 2회 이상 들어오면
         * 막을 수 있도록 분산락을 적용하였다.
         */

        Boolean acquiredLock = lock.tryLock("product_reservation:", String.valueOf(request.reservationId()));

        if (!acquiredLock) {
            throw new RuntimeException("failed to acquire lock.. this is a duplicate request. another request is being processed.. reservationId = " + request.reservationId());
        }

        try {
            ProductReserveResult result = productServiceProxy.tryReserve(request.toCommand());
            return ProductReserveResponse.of(result);
        } finally {
            lock.unlock("product_reservation:", String.valueOf(request.reservationId()));
        }
    }
}
