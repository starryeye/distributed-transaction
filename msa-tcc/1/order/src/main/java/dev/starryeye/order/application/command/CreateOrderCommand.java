package dev.starryeye.order.application.command;

import java.util.List;

public record CreateOrderCommand(
        Long userId,
        List<OrderItem> orderItems
) {

    public record OrderItem(
            Long productId,
            Long orderQuantity
    ) {
    }
}
