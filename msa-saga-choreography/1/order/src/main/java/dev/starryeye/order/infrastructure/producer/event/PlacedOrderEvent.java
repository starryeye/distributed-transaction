package dev.starryeye.order.infrastructure.producer.event;

import java.util.List;

public record PlacedOrderEvent(
        Long userId,
        Long orderId,
        List<OrderItem> orderItems
) {

    public record OrderItem(
            Long productId,
            Long orderQuantity
    ) {
    }
}
