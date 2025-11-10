package dev.starryeye.monolithic_2.order.application;

import dev.starryeye.monolithic_2.order.application.command.CreateOrderCommand;
import dev.starryeye.monolithic_2.order.application.command.PlaceOrderCommand;
import dev.starryeye.monolithic_2.order.application.result.CreateOrderResult;
import dev.starryeye.monolithic_2.order.domain.Order;
import dev.starryeye.monolithic_2.order.domain.OrderItem;
import dev.starryeye.monolithic_2.order.infrastructure.OrderItemRepository;
import dev.starryeye.monolithic_2.order.infrastructure.OrderRepository;
import dev.starryeye.monolithic_2.point.application.PointService;
import dev.starryeye.monolithic_2.product.application.ProductService;
import dev.starryeye.monolithic_2.product.application.command.BuyProductCommand;
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
     * monolithic-1 에서는 시간차를 두고 중복 요청에도 문제가 발생하였지만 (동시성 문제도 존재)
     * monolithic-2 에서는 api 를 두개로 분리하고 주문 status 확인 로직을 적용하여
     *  사용자가 동일한 주문 번호로 시간차를 두고 중복요청을 하면 중복 주문 처리를 막을 수 있게 됨.
     *
     * 하지만, 동시에 주문 처리 api 를 호출하면 여전히 따닥 문제는 존재함..
     * 
     * 현재 OrderController::placeOrder 는 따닥 문제 가능성이 "여전히" 있다.
     * - 동일한 주문이 동시에 요청되면 필터링되지 않고 중복 처리가 된다.
     *
     * 이유는..
     *      DB 의 두번의 갱신 분실 문제(second lost updates problem) 처럼
     *      트랜잭션만으로 동시성 문제를 해결하지 못하는 상황이다.
     *
     * 해결법..
     *      락 도입이 필요하다.
     *      낙관적 락, 비관적 락, redis cache 를 이용한 락.. 등등..
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
    }
}
