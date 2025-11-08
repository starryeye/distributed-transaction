package dev.starryeye.monolithic.order.application;

import dev.starryeye.monolithic.order.application.command.PlaceOrderCommand;
import dev.starryeye.monolithic.order.domain.Order;
import dev.starryeye.monolithic.order.domain.OrderItem;
import dev.starryeye.monolithic.order.infrastructure.OrderItemRepository;
import dev.starryeye.monolithic.order.infrastructure.OrderRepository;
import dev.starryeye.monolithic.point.application.PointService;
import dev.starryeye.monolithic.product.application.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final PointService pointService;
    private final ProductService productService;

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    /**
     * 주문 API 의 따닥 문제 해결법..
     * 1. 주문 생성과 주문 처리를 하나의 API 에서 처리하면..
     *      주문 데이터 자체를 대표하는 하나의 해싱 키를 만들어 중복 필터링을 해야한다.
     * 2. 주문 생성, 주문 처리를 두개의 API 로 나누고
     *      주문 처리 시, 주문 아이디(주문 생성 결과)를 필요하도록 하면 따닥 문제를 회피할 수 있다.
     *
     * 참고
     * 커머스 서비스에서 가장 대표적인 flow 는 2번이다.
     * 장바구니 페이지와 결제 페이지를 나누고..
     *      장바구니 페이지에서 결제 페이지 넘어갈때 주문 생성
     *      결제 페이지에서 결제할때 주문 처리
     */

    @Transactional
    public void placeOrder(PlaceOrderCommand command) {

        Order order = Order.create(command.userId());
        orderRepository.save(order);

        Long totalPrice = 0L;
        for (PlaceOrderCommand.OrderItem item : command.orderItems()) {

            // todo, orderItemRepository::saveAll, productService::buyAll

            OrderItem orderItem = OrderItem.create(order.getId(), item.productId(), item.orderQuantity());
            orderItemRepository.save(orderItem);

            Long price = productService.buy(item.productId(), item.orderQuantity());
            totalPrice += price;
        }

        pointService.use(command.userId(), totalPrice);
    }
}
