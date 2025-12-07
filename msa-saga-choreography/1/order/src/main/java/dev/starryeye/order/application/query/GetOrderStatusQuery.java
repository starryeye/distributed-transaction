package dev.starryeye.order.application.query;

public record GetOrderStatusQuery(
        Long userId,
        Long orderId
) {
}
