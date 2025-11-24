package dev.starryeye.order.infrastructure.product;

import dev.starryeye.order.infrastructure.product.request.BuyProductRequest;
import dev.starryeye.order.infrastructure.product.request.CancelBoughtProductRequest;
import dev.starryeye.order.infrastructure.product.response.BuyProductResponse;
import dev.starryeye.order.infrastructure.product.response.CancelBoughtProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
public class ProductApiClient {

    private final RestClient restClient;

    public BuyProductResponse buyProduct(BuyProductRequest request) {
        return restClient.post()
                .uri("/product/buy")
                .body(request)
                .retrieve()
                .body(BuyProductResponse.class);
    }

    public CancelBoughtProductResponse cancelBoughtProduct(CancelBoughtProductRequest request) {
        return restClient.post()
                .uri("/product/buy/cancel")
                .body(request)
                .retrieve()
                .body(CancelBoughtProductResponse.class);
    }
}
