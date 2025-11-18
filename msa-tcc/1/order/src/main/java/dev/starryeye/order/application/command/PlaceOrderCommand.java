package dev.starryeye.order.application.command;

public record PlaceOrderCommand(
        Long userId,
        Long orderId
) {
}
