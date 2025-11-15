package dev.starryeye.product.controller.request;

import dev.starryeye.product.application.command.ReservedProductConfirmCommand;

public record ReservedProductConfirmRequest(
        String reservationId
) {

    public ReservedProductConfirmCommand toCommand() {
        return new ReservedProductConfirmCommand(reservationId);
    }
}
