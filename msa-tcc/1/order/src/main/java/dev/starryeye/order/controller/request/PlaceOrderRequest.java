package dev.starryeye.order.controller.request;

import dev.starryeye.order.application.command.PlaceOrderCommand;

public record PlaceOrderRequest(
        Long orderId
) {

    public PlaceOrderCommand toCommand(Long userId) {
        return new PlaceOrderCommand(userId, orderId);
    }
}
