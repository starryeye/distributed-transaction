package dev.starryeye.order.infrastructure.point;

import dev.starryeye.order.infrastructure.point.request.CancelUsedPointRequest;
import dev.starryeye.order.infrastructure.point.request.UsePointRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
public class PointApiClient {

    /**
     * retry
     * 네트워크 문제나 일시적인 장애가 발생할 경우나 타임아웃이 발생했지만 실제로는 정상 처리가 되었을 경우에
     * 곧바로 실패처리를 하기보다는 재시도를 통해 오류를 극복시키고 정상 처리로 유도하는 편이 바람직하여 설정함.
     *
     * circuitbreaker
     * api 실패율이 높을때 외부 server 를 차단하기 위함
     * fallback 은 딱히 필요 없이 예외가 발생하도록 둔다.
     */
    private static final String POINT_API_CB = "pointApiCb"; // circuitbreaker
    private static final String POINT_API_RETRY = "pointApiRetry"; // retry

    private final RestClient restClient;

    @Retry(name = POINT_API_RETRY)
    @CircuitBreaker(name = POINT_API_CB)
    public void usePoint(UsePointRequest request) {
        restClient.post()
                .uri("/point/use")
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    @Retry(name = POINT_API_RETRY)
    @CircuitBreaker(name = POINT_API_CB)
    public void cancelUsedPoint(CancelUsedPointRequest request) {
        restClient.post()
                .uri("/point/cancel")
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}
