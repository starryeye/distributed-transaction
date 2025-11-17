package dev.starryeye.order.infrastructure.point.request;

public record PointReserveApiRequest(
        String reservationId,
        Long userId,
        Long reserveBalance
) {
}
