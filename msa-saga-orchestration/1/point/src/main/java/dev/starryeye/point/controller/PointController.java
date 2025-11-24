package dev.starryeye.point.controller;

import dev.starryeye.point.application.PointService;
import dev.starryeye.point.controller.request.CancelUsedPointRequest;
import dev.starryeye.point.controller.request.UsePointRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    @PostMapping("/point/use")
    public void use(@RequestBody UsePointRequest request) {
        pointService.use(request.toCommand());
    }

    @PostMapping("/point/cancel")
    public void cancel(@RequestBody CancelUsedPointRequest request) {
        pointService.cancelUse(request.toCommand());
    }
}
