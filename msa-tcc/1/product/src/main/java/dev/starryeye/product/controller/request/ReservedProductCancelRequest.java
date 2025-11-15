package dev.starryeye.product.controller.request;

import dev.starryeye.product.application.command.ReservedProductCancelCommand;

public record ReservedProductCancelRequest(
        String reservationId
) {

    public ReservedProductCancelCommand toCommand() {
        return new ReservedProductCancelCommand(reservationId);
    }
}
