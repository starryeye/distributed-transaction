package dev.starryeye.product.controller;

import dev.starryeye.product.application.ProductService;
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

    private final ProductService productService;

    private final RedisDistributedLock lock;

    @PostMapping("/product/reserve")
    public ProductReserveResponse reserve(
            @RequestBody ProductReserveRequest request
    ) {

        Boolean acquiredLock = lock.tryLock("product_reservation:", String.valueOf(request.reservationId()));

        if (!acquiredLock) {
            throw new RuntimeException("failed to acquire lock.. this is a duplicate request. another request is being processed.. reservationId = " + request.reservationId());
        }

        try {
            ProductReserveResult result = productService.tryReserve(request.toCommand());
            return ProductReserveResponse.of(result);
        } finally {
            lock.unlock("product_reservation:", String.valueOf(request.reservationId()));
        }
    }
}
