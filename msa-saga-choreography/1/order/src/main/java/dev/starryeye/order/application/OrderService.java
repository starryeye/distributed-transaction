package dev.starryeye.order.application;

import dev.starryeye.order.application.command.CreateOrderCommand;
import dev.starryeye.order.application.command.PlaceOrderCommand;
import dev.starryeye.order.application.query.GetOrderStatusQuery;
import dev.starryeye.order.application.result.CreateOrderResult;
import dev.starryeye.order.domain.Order;
import dev.starryeye.order.domain.OrderItem;
import dev.starryeye.order.infrastructure.OrderItemRepository;
import dev.starryeye.order.infrastructure.OrderRepository;
import dev.starryeye.order.infrastructure.producer.OrderPlacedEventProducer;
import dev.starryeye.order.infrastructure.producer.event.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    private final OrderPlacedEventProducer orderPlacedEventProducer;

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
    public void placeOrder(PlaceOrderCommand command) {

        // orderId 로 order, orderItems 조회
        Order order = orderRepository.findById(command.orderId())
                .orElseThrow(() -> new RuntimeException("order not found, orderId: " + command.orderId()));
        List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(order.getId());

        // order 의 상태를 REQUESTED 로 변경한다. (placeOrder 상태를 의미한다.)
        order.request();

        /**
         * order 가 처리되었다는 이벤트 발행.
         * TransactionSynchronizationManager 를 이용하여 placeOrder 에 걸린 transaction 기준으로 afterCommit 시점에 동작되도록 한다.
         */
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                OrderPlacedEvent orderPlacedEvent = new OrderPlacedEvent(
                        command.userId(),
                        command.orderId(),
                        orderItems.stream()
                                .map(orderItem -> new OrderPlacedEvent.OrderItem(
                                        orderItem.getProductId(),
                                        orderItem.getOrderQuantity()
                                )).toList()
                );

                orderPlacedEventProducer.send(orderPlacedEvent);
            }
        });

    }

    @Transactional
    public void failOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("order not found, orderId: " + orderId));

        order.fail();
    }

    @Transactional
    public void completeOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("order not found, orderId: " + orderId));

        order.complete();
    }

    @Transactional(readOnly = true)
    public String getOrderStatus(GetOrderStatusQuery query) {

        Order order = orderRepository.findByIdAndCustomerId(query.orderId(), query.userId())
                .orElseThrow(() -> new RuntimeException("order not found, orderId: " + query.orderId() + ", customerId: " + query.userId()));

        return order.getStatus();
    }
}
