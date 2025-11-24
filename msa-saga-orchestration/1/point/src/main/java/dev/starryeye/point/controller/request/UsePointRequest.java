package dev.starryeye.point.controller.request;

import dev.starryeye.point.application.command.UsePointCommand;

public record UsePointRequest(
        Long userId,
        String transactionId,
        Long transactionAmount
) {

    public UsePointCommand toCommand() {
        return new UsePointCommand(userId, transactionId, transactionAmount);
    }
}
