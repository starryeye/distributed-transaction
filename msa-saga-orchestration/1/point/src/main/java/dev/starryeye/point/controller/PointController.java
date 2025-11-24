package dev.starryeye.point.controller;

import dev.starryeye.point.application.PointServiceProxy;
import dev.starryeye.point.common.infrastructure.RedisDistributedLock;
import dev.starryeye.point.controller.request.CancelUsedPointRequest;
import dev.starryeye.point.controller.request.UsePointRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PointController {

    private final PointServiceProxy pointService;

    private final RedisDistributedLock lock;

    @PostMapping("/point/use")
    public void use(@RequestBody UsePointRequest request) {
        /**
         * 모종의 이유로 동시에 동일한 transactionId 의 요청이 2회 이상 들어오면
         * 막을 수 있도록 분산락을 적용하였다.
         */

        Boolean acquiredLock = lock.tryLock("point_use:", request.transactionId());

        if (!acquiredLock) {
            throw new RuntimeException("failed to acquire lock.. this is a duplicate request. another request is being processed.. transactionId = " + request.transactionId());
        }

        try {
            pointService.use(request.toCommand());
        } finally {
            lock.unlock("point_use:", request.transactionId());
        }
    }

    @PostMapping("/point/cancel")
    public void cancel(@RequestBody CancelUsedPointRequest request) {
        /**
         * 모종의 이유로 동시에 동일한 transactionId 의 요청이 2회 이상 들어오면
         * 막을 수 있도록 분산락을 적용하였다.
         * 그리고, use 단계와 동시에 실행되는 것을 방지하고자 use 단계에서 사용된 key 를 사용한다.
         */

        Boolean acquiredLock = lock.tryLock("point_use:", request.transactionId());

        if (!acquiredLock) {
            throw new RuntimeException("failed to acquire lock.. this is a duplicate request. another request is being processed.. transactionId = " + request.transactionId());
        }

        try {
            pointService.cancelUse(request.toCommand());
        } finally {
            lock.unlock("point_use:", request.transactionId());
        }
    }
}
