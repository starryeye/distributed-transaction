package dev.starryeye.order.controller.request;

import dev.starryeye.order.application.command.CreateOrderCommand;

import java.util.List;

public record CreateOrderRequest(
        List<Item> orderItems
) {

    public record Item(
            Long productId,
            Long orderQuantity
    ) {
    }

    public CreateOrderCommand toCommand(Long userId) {
        return new CreateOrderCommand(
                userId,
                orderItems.stream()
                        .map(orderItem -> new CreateOrderCommand.Item(orderItem.productId, orderItem.orderQuantity))
                        .toList()
        );
    }
}
