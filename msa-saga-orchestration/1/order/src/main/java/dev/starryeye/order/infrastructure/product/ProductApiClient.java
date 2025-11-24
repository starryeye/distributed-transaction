package dev.starryeye.order.infrastructure.product;

import dev.starryeye.order.infrastructure.product.request.BuyProductRequest;
import dev.starryeye.order.infrastructure.product.request.CancelBoughtProductRequest;
import dev.starryeye.order.infrastructure.product.response.BuyProductResponse;
import dev.starryeye.order.infrastructure.product.response.CancelBoughtProductResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
public class ProductApiClient {

    private static final String PRODUCT_API_CB = "productApiCb"; // circuitbreaker
    private static final String PRODUCT_API_RETRY = "productApiRetry"; // retry

    private final RestClient restClient;

    @Retry(name = PRODUCT_API_RETRY)
    @CircuitBreaker(name = PRODUCT_API_CB)
    public BuyProductResponse buyProduct(BuyProductRequest request) {
        return restClient.post()
                .uri("/product/buy")
                .body(request)
                .retrieve()
                .body(BuyProductResponse.class);
    }

    @Retry(name = PRODUCT_API_RETRY)
    @CircuitBreaker(name = PRODUCT_API_CB)
    public CancelBoughtProductResponse cancelBoughtProduct(CancelBoughtProductRequest request) {
        return restClient.post()
                .uri("/product/buy/cancel")
                .body(request)
                .retrieve()
                .body(CancelBoughtProductResponse.class);
    }
}
