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
