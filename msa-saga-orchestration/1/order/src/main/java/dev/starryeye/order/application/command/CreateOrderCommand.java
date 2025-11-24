package dev.starryeye.order.application.command;

import java.util.List;

public record CreateOrderCommand(
        Long userId,
        List<Item> orderItems
) {
    public record Item(
            Long productId,
            Long orderQuantity
    ) {
    }
}
