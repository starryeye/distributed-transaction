package dev.starryeye.product.infrastructure.producer.event;

public record ProductBoughtEvent(
        Long userId,
        Long orderId,
        Long totalBoughtPrice
) {
}
