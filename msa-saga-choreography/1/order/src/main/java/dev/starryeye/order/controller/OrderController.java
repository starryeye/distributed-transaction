package dev.starryeye.order.controller;

import dev.starryeye.order.application.OrderService;
import dev.starryeye.order.application.query.GetOrderStatusQuery;
import dev.starryeye.order.application.result.CreateOrderResult;
import dev.starryeye.order.common.infrastructure.RedisDistributedLock;
import dev.starryeye.order.controller.request.CreateOrderRequest;
import dev.starryeye.order.controller.request.PlaceOrderRequest;
import dev.starryeye.order.controller.response.CreateOrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/order/{orderId}/status")
    public String getOrderStatus(
            @PathVariable("orderId") Long orderId,
            @RequestHeader("X-User-Id") Long userId
    ) {

        /**
         * TCC, SAGA-orchestration 방식에서는
         *      사용자가 주문결제(placeOrder)를 진행하면, 동기식으로 전체 처리를 진행 후 전체 처리 결과를 응답해준다.
         * 하자만, SAGA-choreography 방식에서는
         *      사용자가 주문결제(placeOrder)를 진행하면, 주문 요청까지만 처리하고 나머지 전체 처리는 비동기로 진행되기 때문에
         *      주문의 상태를 볼 수 있도록 지원해줘야한다.
         */

        GetOrderStatusQuery query = new GetOrderStatusQuery(userId, orderId);
        return orderService.getOrderStatus(query);
    }
}
