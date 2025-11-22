package dev.starryeye.product.controller.response;

import dev.starryeye.product.application.result.BuyProductResult;

public record BuyProductResponse(
        Long totalBoughtPrice
) {

    public static BuyProductResponse from(BuyProductResult result) {
        return new BuyProductResponse(result.totalBoughtPrice());
    }
}
