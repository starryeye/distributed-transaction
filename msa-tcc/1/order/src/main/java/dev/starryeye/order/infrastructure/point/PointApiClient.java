package dev.starryeye.order.infrastructure.point;

import dev.starryeye.order.infrastructure.point.request.PointReserveApiRequest;
import dev.starryeye.order.infrastructure.point.request.ReservedPointCancelApiRequest;
import dev.starryeye.order.infrastructure.point.request.ReservedPointConfirmApiRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
public class PointApiClient {

    private final RestClient restClient;

    public void reservePoint(PointReserveApiRequest request) {
        restClient.post()
                .uri("/point/reserve")
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    public void confirmPoint(ReservedPointConfirmApiRequest request) {
        restClient.post()
                .uri("/point/confirm")
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    public void cancelPoint(ReservedPointCancelApiRequest request) {
        restClient.post()
                .uri("/point/cancel")
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}
