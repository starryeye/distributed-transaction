package dev.starryeye.monolithic.order.controller.request;

import dev.starryeye.monolithic.order.application.command.PlaceOrderCommand;

import java.util.List;

public record PlaceOrderRequest(
        List<OrderItem> orderItems
) {
    public record OrderItem(
            Long productId,
            Long orderQuantity
    ) {
    }

    public PlaceOrderCommand toCommand(Long userId) {
        return new PlaceOrderCommand(
                userId,
                orderItems.stream()
                        .map(orderItem -> new PlaceOrderCommand.OrderItem(
                                orderItem.productId(),
                                orderItem.orderQuantity()
                        ))
                        .toList()
        );
    }
}
