package dev.starryeye.order.application;

import dev.starryeye.order.application.command.CreateOrderCommand;
import dev.starryeye.order.application.result.CreateOrderResult;
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
    public CreateOrderResult create(CreateOrderCommand command) {
        // 주문 생성
        Order order = Order.create(command.userId());
        orderRepository.save(order);

        // 주문 상세 생성
        List<OrderItem> orderItems = command.orderItems().stream()
                .map(orderItemCommand -> OrderItem.create(order.getId(), orderItemCommand.productId(), orderItemCommand.orderQuantity()))
                .toList();
        orderItemRepository.saveAll(orderItems);

        return new CreateOrderResult(order.getId());
    }
}
