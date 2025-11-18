package dev.starryeye.order.controller;

import dev.starryeye.order.application.OrderCoordinator;
import dev.starryeye.order.common.infrastructure.RedisDistributedLock;
import dev.starryeye.order.controller.request.PlaceOrderRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderCoordinatorController {

    private final OrderCoordinator orderCoordinator;

    private final RedisDistributedLock lock;

    @PostMapping("/order/place")
    public void placeOrder(
            @RequestBody PlaceOrderRequest request,
            @RequestHeader("X-User-Id") Long userId
    ) {

        /**
         * 모종의 이유로 동시에 동일한 orderId 의 요청이 2회 이상 들어오면
         * 막을 수 있도록 분산락을 적용하였다.
         */

        Boolean acquiredLock = lock.tryLock("order:", request.orderId().toString());

        if (!acquiredLock) {
            throw new RuntimeException("failed to acquire lock.. this is a duplicate request. another request is being processed.. orderId = " + request.orderId());
        }

        try {
            orderCoordinator.placeOrder(request.toCommand(userId));
        } finally {
            lock.unlock("order:", request.orderId().toString());
        }
    }
}
