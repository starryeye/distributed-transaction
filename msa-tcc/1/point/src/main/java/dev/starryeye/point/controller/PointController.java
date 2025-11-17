package dev.starryeye.point.controller;

import dev.starryeye.point.application.PointService;
import dev.starryeye.point.common.infrastructure.RedisDistributedLock;
import dev.starryeye.point.controller.request.PointReserveRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    private final RedisDistributedLock lock;

    @PostMapping("/point/reserve")
    public void reserve(@RequestBody PointReserveRequest request) {

        /**
         * 모종의 이유로 동시에 동일한 reservationId 의 요청이 2회 이상 들어오면
         * 막을 수 있도록 분산락을 적용하였다.
         */

        Boolean acquiredLock = lock.tryLock("point_reservation:", request.reservationId());

        if (!acquiredLock) {
            throw new RuntimeException("failed to acquire lock.. this is a duplicate request. another request is being processed.. reservationId = " + request.reservationId());
        }

        try {
            pointService.tryReserve(request.toCommand());
        } finally {
            lock.unlock("point_reservation:", request.reservationId());
        }
    }
}
