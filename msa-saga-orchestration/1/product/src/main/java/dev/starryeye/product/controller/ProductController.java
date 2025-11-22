package dev.starryeye.product.controller;

import dev.starryeye.product.application.ProductServiceProxy;
import dev.starryeye.product.application.result.BuyProductResult;
import dev.starryeye.product.application.result.CancelBoughtProductResult;
import dev.starryeye.product.common.infrastructure.RedisDistributedLock;
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

    private final ProductServiceProxy productService;

    private final RedisDistributedLock lock;

    @PostMapping("/product/buy")
    public BuyProductResponse buy(@RequestBody BuyProductRequest request) {
        /**
         * 모종의 이유로 동시에 동일한 boughtId 의 요청이 2회 이상 들어오면
         * 막을 수 있도록 분산락을 적용하였다.
         */

        Boolean acquiredLock = lock.tryLock("product_buy:", request.boughtId());

        if (!acquiredLock) {
            throw new RuntimeException("failed to acquire lock.. this is a duplicate request. another request is being processed.. boughtId = " + request.boughtId());
        }

        try {
            BuyProductResult result = productService.buy(request.toCommand());
            return BuyProductResponse.from(result);
        } finally {
            lock.unlock("product_buy:", request.boughtId());
        }
    }

    @PostMapping("/product/buy/cancel")
    public CancelBoughtProductResponse cancelBuying(@RequestBody CancelBoughtProductRequest request) {
        /**
         * 모종의 이유로 동시에 동일한 boughtId 의 요청이 2회 이상 들어오면
         * 막을 수 있도록 분산락을 적용하였다.
         * 그리고, buy 단계와 동시에 실행되는 것을 방지하고자 buy 단계에서 사용된 key 를 사용한다.
         */

        Boolean acquiredLock = lock.tryLock("product_buy:", request.boughtId());

        if (!acquiredLock) {
            throw new RuntimeException("failed to acquire lock.. this is a duplicate request. another request is being processed.. boughtId = " + request.boughtId());
        }

        try {
            CancelBoughtProductResult result = productService.cancelBuying(request.toCommand());
            return CancelBoughtProductResponse.from(result);
        } finally {
            lock.unlock("product_buy:", request.boughtId());
        }
    }
}
