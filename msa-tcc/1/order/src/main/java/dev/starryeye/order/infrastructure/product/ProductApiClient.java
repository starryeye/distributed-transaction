package dev.starryeye.order.infrastructure.product;

import dev.starryeye.order.infrastructure.product.request.ProductReserveApiRequest;
import dev.starryeye.order.infrastructure.product.request.ReservedProductCancelApiRequest;
import dev.starryeye.order.infrastructure.product.request.ReservedProductConfirmApiRequest;
import dev.starryeye.order.infrastructure.product.response.ProductReserveApiResponse;
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
    public ProductReserveApiResponse reserveProduct(ProductReserveApiRequest request) {
        return restClient.post()
                .uri("/product/reserve")
                .body(request)
                .retrieve()
                .body(ProductReserveApiResponse.class);
    }

    @Retry(name = PRODUCT_API_RETRY)
    @CircuitBreaker(name = PRODUCT_API_CB)
    public void confirmProduct(ReservedProductConfirmApiRequest request) {
        restClient.post()
                .uri("/product/confirm")
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    @Retry(name = PRODUCT_API_RETRY)
    @CircuitBreaker(name = PRODUCT_API_CB)
    public void cancelProduct(ReservedProductCancelApiRequest request) {
        restClient.post()
                .uri("/product/cancel")
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}
