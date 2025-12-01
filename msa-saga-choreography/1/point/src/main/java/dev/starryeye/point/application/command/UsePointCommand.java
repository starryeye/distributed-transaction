package dev.starryeye.point.application.command;

public record UsePointCommand(
        Long userId,
        String transactionId,
        Long transactionAmount
) {
}
