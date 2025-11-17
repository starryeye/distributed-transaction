package dev.starryeye.point.controller.request;

import dev.starryeye.point.application.command.ReservedPointCancelCommand;

public record ReservedPointCancelRequest(
        String reservationId
) {

    public ReservedPointCancelCommand toCommand() {
        return new ReservedPointCancelCommand(reservationId);
    }
}
