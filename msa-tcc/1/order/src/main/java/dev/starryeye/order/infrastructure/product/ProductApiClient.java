package dev.starryeye.order.infrastructure.product;

import dev.starryeye.order.infrastructure.product.request.ProductReserveApiRequest;
import dev.starryeye.order.infrastructure.product.request.ReservedProductCancelApiRequest;
import dev.starryeye.order.infrastructure.product.request.ReservedProductConfirmApiRequest;
import dev.starryeye.order.infrastructure.product.response.ProductReserveApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
public class ProductApiClient {

    private final RestClient restClient;

    public ProductReserveApiResponse reserveProduct(ProductReserveApiRequest request) {
        return restClient.post()
                .uri("/product/reserve")
                .body(request)
                .retrieve()
                .body(ProductReserveApiResponse.class);
    }

    public void confirmProduct(ReservedProductConfirmApiRequest request) {
        restClient.post()
                .uri("/product/confirm")
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    public void cancelProduct(ReservedProductCancelApiRequest request) {
        restClient.post()
                .uri("/product/cancel")
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}
