package dev.starryeye.point.controller;

import dev.starryeye.point.application.PointService;
import dev.starryeye.point.controller.request.PointReserveRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    @PostMapping("/point/reserve")
    public void reserve(@RequestBody PointReserveRequest request) {

        pointService.tryReserve(request.toCommand());
    }
}
