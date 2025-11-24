package dev.starryeye.order.infrastructure.point.request;

public record UsePointRequest(
        Long userId,
        String transactionId,
        Long transactionAmount
) {
}
