package dev.starryeye.product.controller;

import dev.starryeye.product.application.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/product/reserve")
    public void reserve() {
        // todo
    }
}
