package dev.starryeye.product.controller.response;

import dev.starryeye.product.application.result.CancelBoughtProductResult;

public record CancelBoughtProductResponse(
        Long cancelledTotalBoughtPrice
) {

    public static CancelBoughtProductResponse from(CancelBoughtProductResult result) {
        return new CancelBoughtProductResponse(result.cancelledTotalBoughtPrice());
    }
}
