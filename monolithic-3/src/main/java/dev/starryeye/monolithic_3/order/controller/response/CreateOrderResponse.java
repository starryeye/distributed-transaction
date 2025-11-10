package dev.starryeye.monolithic_3.order.controller.response;

import dev.starryeye.monolithic_3.order.application.result.CreateOrderResult;

public record CreateOrderResponse(
        Long orderId
) {

    public static CreateOrderResponse of(CreateOrderResult result) {
        return new CreateOrderResponse(result.orderId());
    }
}
