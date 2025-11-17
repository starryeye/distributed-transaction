package dev.starryeye.point.application.command;

public record PointReserveCommand(
        String reservationId,
        Long userId,
        Long reserveBalance
) {
}
