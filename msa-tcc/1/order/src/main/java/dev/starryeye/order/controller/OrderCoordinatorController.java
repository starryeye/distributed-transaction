package dev.starryeye.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderCoordinatorController {

    @PostMapping("/order/place")
    public void placeOrder() {
        // todo
    }
}
