package dev.starryeye.order.controller.response;

import dev.starryeye.order.application.result.CreateOrderResult;

public record CreateOrderResponse(
        Long orderId
) {

    public static CreateOrderResponse from(CreateOrderResult result) {
        return new CreateOrderResponse(result.orderId());
    }
}
