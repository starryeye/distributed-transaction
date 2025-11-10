package dev.starryeye.monolithic_2.order.controller.request;

import dev.starryeye.monolithic_2.order.application.command.CreateOrderCommand;

import java.util.List;

public record CreateOrderRequest(
        List<OrderItem> orderItems
) {

    public record OrderItem(
            Long productId,
            Long orderQuantity
    ) {
    }

    public CreateOrderCommand toCommand(Long userId) {

        return new CreateOrderCommand(
                userId,
                orderItems.stream()
                        .map(orderItem -> new CreateOrderCommand.OrderItem(
                                orderItem.productId(),
                                orderItem.orderQuantity()
                        ))
                        .toList()
        );
    }
}
