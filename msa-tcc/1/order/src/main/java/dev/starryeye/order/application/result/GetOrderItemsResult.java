package dev.starryeye.order.application.result;

import java.util.List;

public record GetOrderItemsResult(
        List<OrderItem> items
) {

    public record OrderItem(
            Long productId,
            Long orderQuantity
    ) {
    }
}
