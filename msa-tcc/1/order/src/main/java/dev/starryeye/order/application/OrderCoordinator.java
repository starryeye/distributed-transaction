package dev.starryeye.order.application;

import dev.starryeye.order.infrastructure.point.PointApiClient;
import dev.starryeye.order.infrastructure.product.ProductApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderCoordinator {

    private final OrderService orderService;

    private final ProductApiClient productApiClient;
    private final PointApiClient pointApiClient;

    // todo
}
