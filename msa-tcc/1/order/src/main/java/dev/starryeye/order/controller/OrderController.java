package dev.starryeye.order.controller;

import dev.starryeye.order.application.OrderService;
import dev.starryeye.order.application.result.CreateOrderResult;
import dev.starryeye.order.controller.request.CreateOrderRequest;
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

    @PostMapping("/order/new")
    public CreateOrderResponse create(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody CreateOrderRequest request
    ) {
        CreateOrderResult createOrderResult = orderService.create(request.toCommand(userId));
        return CreateOrderResponse.of(createOrderResult);
    }
}
