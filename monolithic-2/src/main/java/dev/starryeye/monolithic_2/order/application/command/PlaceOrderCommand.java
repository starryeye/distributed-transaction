package dev.starryeye.monolithic_2.order.application.command;

public record PlaceOrderCommand(
        Long userId,
        Long orderId
) {
}
