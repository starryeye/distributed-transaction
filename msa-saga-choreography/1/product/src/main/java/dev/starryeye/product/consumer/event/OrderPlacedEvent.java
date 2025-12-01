package dev.starryeye.product.consumer.event;

import java.util.List;

public record OrderPlacedEvent(
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
