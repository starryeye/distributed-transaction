package dev.starryeye.product.controller;

import dev.starryeye.product.application.ProductService;
import dev.starryeye.product.application.result.BuyProductResult;
import dev.starryeye.product.application.result.CancelBoughtProductResult;
import dev.starryeye.product.controller.request.BuyProductRequest;
import dev.starryeye.product.controller.request.CancelBoughtProductRequest;
import dev.starryeye.product.controller.response.BuyProductResponse;
import dev.starryeye.product.controller.response.CancelBoughtProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/product/buy")
    public BuyProductResponse buy(@RequestBody BuyProductRequest request) {
        BuyProductResult result = productService.buy(request.toCommand());
        return BuyProductResponse.from(result);
    }

    @PostMapping("/product/buy/cancel")
    public CancelBoughtProductResponse cancelBuying(@RequestBody CancelBoughtProductRequest request) {
        CancelBoughtProductResult result = productService.cancelBuying(request.toCommand());
        return CancelBoughtProductResponse.from(result);
    }
}
