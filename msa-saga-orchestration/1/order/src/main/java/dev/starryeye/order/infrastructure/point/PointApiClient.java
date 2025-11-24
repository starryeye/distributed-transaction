package dev.starryeye.order.infrastructure.point;

import dev.starryeye.order.infrastructure.point.request.CancelUsedPointRequest;
import dev.starryeye.order.infrastructure.point.request.UsePointRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
public class PointApiClient {

    private final RestClient restClient;

    public void usePoint(UsePointRequest request) {
        restClient.post()
                .uri("/point/use")
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    public void cancelUsedPoint(CancelUsedPointRequest request) {
        restClient.post()
                .uri("/point/cancel")
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}
