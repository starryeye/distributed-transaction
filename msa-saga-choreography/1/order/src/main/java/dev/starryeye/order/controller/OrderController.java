package dev.starryeye.order.controller;

import dev.starryeye.order.application.OrderService;
import dev.starryeye.order.application.result.CreateOrderResult;
import dev.starryeye.order.common.infrastructure.RedisDistributedLock;
import dev.starryeye.order.controller.request.CreateOrderRequest;
import dev.starryeye.order.controller.request.PlaceOrderRequest;
import dev.starryeye.order.controller.response.CreateOrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    private final RedisDistributedLock lock;

    @PostMapping("/order/new")
    public CreateOrderResponse create(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody CreateOrderRequest request
    ) {
        CreateOrderResult result = orderService.createOrder(request.toCommand(userId));
        return CreateOrderResponse.from(result);
    }

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
            orderService.placeOrder(request.toCommand(userId));
        } finally {
            lock.unlock("order:", request.orderId().toString());
        }
    }
}
