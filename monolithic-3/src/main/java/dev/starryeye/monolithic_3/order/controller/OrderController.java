package dev.starryeye.monolithic_3.order.controller;

import dev.starryeye.monolithic_3.common.infrastructure.RedisDistributedLock;
import dev.starryeye.monolithic_3.order.application.OrderService;
import dev.starryeye.monolithic_3.order.application.result.CreateOrderResult;
import dev.starryeye.monolithic_3.order.controller.request.CreateOrderRequest;
import dev.starryeye.monolithic_3.order.controller.request.PlaceOrderRequest;
import dev.starryeye.monolithic_3.order.controller.response.CreateOrderResponse;
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
    public CreateOrderResponse createOrder(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody CreateOrderRequest request
    ) {
        CreateOrderResult createOrderResult = orderService.createOrder(request.toCommand(userId));
        return CreateOrderResponse.of(createOrderResult);
    }

    @PostMapping("/order/place")
    public void placeOrder(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody PlaceOrderRequest request
    ) {

        Boolean acquiredLock = lock.tryLock("order:", String.valueOf(request.orderId()));

        if (!acquiredLock) {
            throw new RuntimeException("failed to acquire lock.. this is a duplicate request. another request is being processed..");
        }

        try {
            orderService.placeOrder(request.toCommand(userId));
        } finally {
            lock.unlock("order:", String.valueOf(request.orderId()));
        }
    }
}
