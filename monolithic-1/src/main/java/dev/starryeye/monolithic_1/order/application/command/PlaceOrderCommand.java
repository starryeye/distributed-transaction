package dev.starryeye.monolithic_1.order.application.command;

import java.util.List;

public record PlaceOrderCommand(
        Long userId,
        List<OrderItem> orderItems
) {

    public record OrderItem(
            Long productId,
            Long orderQuantity
    ) {
    }
}
