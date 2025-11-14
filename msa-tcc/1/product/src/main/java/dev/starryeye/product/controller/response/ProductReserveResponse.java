package dev.starryeye.product.controller.response;

import dev.starryeye.product.application.result.ProductReserveResult;

public record ProductReserveResponse(
        Long totalPrice
) {

    public static ProductReserveResponse of(ProductReserveResult result) {
        return new ProductReserveResponse(result.totalPrice());
    }
}
