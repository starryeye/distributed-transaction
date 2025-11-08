package dev.starryeye.monolithic.order.controller;

import dev.starryeye.monolithic.order.application.OrderService;
import dev.starryeye.monolithic.order.controller.request.PlaceOrderRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/order/place")
    public void placeOrder(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody PlaceOrderRequest request
    ) {
        orderService.placeOrder(request.toCommand(userId));
    }
}
