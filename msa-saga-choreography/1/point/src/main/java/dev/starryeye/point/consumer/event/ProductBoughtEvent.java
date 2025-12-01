package dev.starryeye.point.consumer.event;

public record ProductBoughtEvent(
        Long userId,
        Long orderId,
        Long totalBoughtPrice
) {
}
