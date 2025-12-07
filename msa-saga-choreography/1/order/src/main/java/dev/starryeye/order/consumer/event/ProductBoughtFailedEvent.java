package dev.starryeye.order.consumer.event;

public record ProductBoughtFailedEvent(
        Long orderId,
        Long cancelledTotalBoughtPrice
) {
}
