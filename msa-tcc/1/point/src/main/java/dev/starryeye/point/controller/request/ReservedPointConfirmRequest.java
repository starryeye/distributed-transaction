package dev.starryeye.point.controller.request;

import dev.starryeye.point.application.command.ReservedPointConfirmCommand;

public record ReservedPointConfirmRequest(
        String reservationId
) {

    public ReservedPointConfirmCommand toCommand() {
        return new ReservedPointConfirmCommand(reservationId);
    }
}
