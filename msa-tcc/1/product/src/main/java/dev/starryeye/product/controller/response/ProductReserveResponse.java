package dev.starryeye.product.controller.response;

import dev.starryeye.product.application.result.ProductReserveResult;

public record ProductReserveResponse(
        Long totalReservedPrice
) {

    public static ProductReserveResponse of(ProductReserveResult result) {
        return new ProductReserveResponse(result.totalReservedPrice());
    }
}
