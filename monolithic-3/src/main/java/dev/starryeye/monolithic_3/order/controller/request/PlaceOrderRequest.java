package dev.starryeye.monolithic_3.order.controller.request;

import dev.starryeye.monolithic_3.order.application.command.PlaceOrderCommand;

public record PlaceOrderRequest(
        Long orderId
) {

    public PlaceOrderCommand toCommand(Long userId) {
        return new PlaceOrderCommand(
                userId,
                orderId
        );
    }
}
