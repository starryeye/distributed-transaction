package dev.starryeye.product.infrastructure.producer.event;

public record ProductBoughtFailedEvent(
        Long orderId,
        Long cancelledTotalBoughtPrice
) {
}
