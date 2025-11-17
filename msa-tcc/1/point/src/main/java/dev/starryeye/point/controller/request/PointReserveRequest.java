package dev.starryeye.point.controller.request;

import dev.starryeye.point.application.command.PointReserveCommand;

public record PointReserveRequest(
        String reservationId,
        Long userId,
        Long reserveBalance
) {

    public PointReserveCommand toCommand() {
        return new PointReserveCommand(reservationId, userId, reserveBalance);
    }
}
