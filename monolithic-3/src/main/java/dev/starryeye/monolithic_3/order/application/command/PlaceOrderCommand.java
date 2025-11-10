package dev.starryeye.monolithic_3.order.application.command;

public record PlaceOrderCommand(
        Long userId,
        Long orderId
) {
}
