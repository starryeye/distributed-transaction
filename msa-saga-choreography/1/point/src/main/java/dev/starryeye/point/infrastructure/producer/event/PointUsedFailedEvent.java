package dev.starryeye.point.infrastructure.producer.event;

public record PointUsedFailedEvent(
        Long orderId
) {
}
