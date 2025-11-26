package dev.starryeye.order.application;

import dev.starryeye.order.application.command.CreateOrderCommand;
import dev.starryeye.order.application.result.CreateOrderResult;
import dev.starryeye.order.application.result.GetOrderItemsResult;
import dev.starryeye.order.domain.Order;
import dev.starryeye.order.domain.OrderItem;
import dev.starryeye.order.infrastructure.OrderItemRepository;
import dev.starryeye.order.infrastructure.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional
    public CreateOrderResult createOrder(CreateOrderCommand command) {

        Order order = Order.create(command.userId());
        orderRepository.save(order);

        List<OrderItem> orderItems = command.orderItems().stream()
                .map(item -> OrderItem.create(order.getId(), item.productId(), item.orderQuantity()))
                .toList();
        orderItemRepository.saveAll(orderItems);

        return new CreateOrderResult(order.getId());
    }

    @Transactional
    public void requestOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("order not found, orderId: " + orderId));

        order.request();
    }

    @Transactional
    public void completeRequestedOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("order not found, orderId: " + orderId));

        order.complete();
    }

    @Transactional
    public void failRequestedOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("order not found, orderId: " + orderId));

        order.fail();
    }

    @Transactional(readOnly = true)
    public GetOrderItemsResult getOrderItems(Long orderId) {

        List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(orderId);

        return new GetOrderItemsResult(
                orderItems.stream()
                        .map(orderItem -> new GetOrderItemsResult.OrderItem(
                                orderItem.getProductId(), orderItem.getOrderQuantity()
                        )).toList()
        );
    }
}
