package dev.starryeye.monolithic.point.application;

import dev.starryeye.monolithic.point.domain.Point;
import dev.starryeye.monolithic.point.infrastructure.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;

    @Transactional
    public void use(Long userId, Long useAmount) {

        Point point = pointRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("point not found, userId: " + userId));

        point.reduce(useAmount);
    }
}
