package dev.starryeye.point.controller.request;

import dev.starryeye.point.application.command.CancelUsedPointCommand;

public record CancelUsedPointRequest(
        String transactionId
) {

    public CancelUsedPointCommand toCommand() {
        return new CancelUsedPointCommand(transactionId);
    }
}
