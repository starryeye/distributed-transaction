package dev.starryeye.monolithic_2.order.controller.request;

import dev.starryeye.monolithic_2.order.application.command.PlaceOrderCommand;

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
