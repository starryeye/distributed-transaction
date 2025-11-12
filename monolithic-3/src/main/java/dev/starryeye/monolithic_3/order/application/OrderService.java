package dev.starryeye.monolithic_3.order.application;

import dev.starryeye.monolithic_3.order.application.command.CreateOrderCommand;
import dev.starryeye.monolithic_3.order.application.command.PlaceOrderCommand;
import dev.starryeye.monolithic_3.order.application.result.CreateOrderResult;
import dev.starryeye.monolithic_3.order.domain.Order;
import dev.starryeye.monolithic_3.order.domain.OrderItem;
import dev.starryeye.monolithic_3.order.infrastructure.OrderItemRepository;
import dev.starryeye.monolithic_3.order.infrastructure.OrderRepository;
import dev.starryeye.monolithic_3.point.application.PointService;
import dev.starryeye.monolithic_3.product.application.ProductService;
import dev.starryeye.monolithic_3.product.application.command.BuyProductCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final PointService pointService;
    private final ProductService productService;

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    /**
     * monolithic-2 에서는 api 를 두개로 분리하고 주문 status 확인 로직을 적용하여
     *  사용자가 동일한 주문 번호로 시간차를 두고 중복요청을 하면 중복 주문 처리를 막을 수 있게 되었지만..
     *  여전히, 동시성 문제가 존재한다.
     *
     * 이를 redis 의 setnx 를 응용하여 락으로 해결해본다.
     * -> 구현 편의를 위해 OrderController 에서 처리함.
     */

    @Transactional
    public CreateOrderResult createOrder(CreateOrderCommand command) {
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

    @Transactional
    public void placeOrder(PlaceOrderCommand command) {

        Order order = orderRepository.findById(command.orderId())
                .orElseThrow(() -> new IllegalArgumentException("order not found, id: " + command.orderId()));

        if (order.isComplete()) {
            return;
        }

        List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(order.getId());

        // 주문 처리(재고 관리(차감))
        BuyProductCommand buyProductCommand = new BuyProductCommand(orderItems.stream()
                .map(item -> new BuyProductCommand.Product(item.getProductId(), item.getOrderQuantity()))
                .toList()
        );
        Long totalPrice = productService.buyAll(buyProductCommand);

        // 주문 처리(결제)
        pointService.use(command.userId(), totalPrice);

        order.complete();

        // 동시 요청 test 용
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
    }
}
